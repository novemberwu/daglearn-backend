#!/usr/bin/env bash

# Exit immediately if any command fails
set -e

# Define log and backup folders
LOG_DIR="logs"
mkdir -p "$LOG_DIR"

TIMESTAMP=$(date +%Y%m%d_%H%M%S)
BACKUP_FILE="${LOG_DIR}/railway_backup_${TIMESTAMP}.json"
DIFF_REPORT="${LOG_DIR}/railway_diff_report_${TIMESTAMP}.txt"
ROLLBACK_SCRIPT="${LOG_DIR}/rollback_railway_config_${TIMESTAMP}.sh"

# Color Codes for Terminal Output
GREEN='\033[0;32m'
RED='\033[0;31m'
YELLOW='\033[0;33m'
BLUE='\033[0;34m'
CYAN='\033[0;36m'
NC='\033[0m' # No Color

echo -e "${CYAN}====================================================${NC}"
echo -e "${CYAN}🚀 Railway Variable Configuration & Deployment Safety Script${NC}"
echo -e "${CYAN}====================================================${NC}"

# Ensure Railway CLI is installed
if ! command -v railway &> /dev/null; then
    echo -e "${RED}Error: Railway CLI is not installed on this system.${NC}"
    echo -e "Please install it using: npm install -g @railway/cli"
    exit 1
fi

# Ensure .env_production exists
if [ ! -f ".env_production" ]; then
    echo -e "${RED}Error: .env_production file not found at the root of the project.${NC}"
    echo -e "Please create it using the template provided in your issues or memory files."
    exit 1
fi

echo -e "${BLUE}[Step 1/5] Backing up current production Railway variables...${NC}"
# Fetch current variables in JSON format
if ! railway variables --json > "$BACKUP_FILE" 2>/dev/null; then
    echo -e "${RED}Error: Failed to fetch environment variables from Railway.${NC}"
    echo -e "Make sure you are logged in (${YELLOW}railway login${NC}) and the project is linked (${YELLOW}railway link${NC})."
    # Clean up empty backup file if any
    rm -f "$BACKUP_FILE"
    exit 1
fi

echo -e "${GREEN}✓ Production variables backed up successfully to: ${BACKUP_FILE}${NC}"

echo -e "${BLUE}[Step 2/5] Comparing .env_production with current Railway state...${NC}"

# Python comparison and script generation snippet
python3 - <<EOF
import os
import sys
import json

# Configuration paths
backup_file = "${BACKUP_FILE}"
env_file = ".env_production"
diff_report_path = "${DIFF_REPORT}"
rollback_script_path = "${ROLLBACK_SCRIPT}"

# Color codes (duplicate inside Python for printing)
GREEN = '\033[0;32m'
RED = '\033[0;31m'
YELLOW = '\033[0;33m'
BLUE = '\033[0;34m'
CYAN = '\033[0;36m'
NC = '\033[0m'

def parse_env_file(filepath):
    env_vars = {}
    if not os.path.exists(filepath):
        return env_vars
    with open(filepath, 'r') as f:
        for line in f:
            line = line.strip()
            if not line or line.startswith('#'):
                continue
            if '=' in line:
                parts = line.split('=', 1)
                key = parts[0].strip()
                val = parts[1].strip()
                if (val.startswith('"') and val.endswith('"')) or (val.startswith("'") and val.endswith("'")):
                    val = val[1:-1]
                env_vars[key] = val
    return env_vars

try:
    # Load remote/current variables
    with open(backup_file, 'r') as f:
        remote_vars = json.load(f)
except Exception as e:
    print(f"{RED}Error loading remote backup JSON: {e}{NC}")
    sys.exit(1)

# Load local desired variables
local_vars = parse_env_file(env_file)

# Tracking differences
adds = {}
modifies = {}
deletes = {} # Exists remotely but missing locally
unchanged = {}

for key, desired_val in local_vars.items():
    if key in remote_vars:
        current_val = remote_vars[key]
        if desired_val != current_val:
            modifies[key] = {"old": current_val, "new": desired_val}
        else:
            unchanged[key] = desired_val
    else:
        adds[key] = desired_val

for key, current_val in remote_vars.items():
    if key not in local_vars:
        deletes[key] = current_val

# Write the Diff Report
with open(diff_report_path, 'w') as f:
    f.write("=== RAILWAY VARIABLE CONFIGURATION DIFF REPORT ===\n\n")
    f.write(f"ADDITIONS ([+]): {len(adds)}\n")
    for k, v in adds.items():
        f.write(f"  + {k}={v}\n")
    
    f.write(f"\nMODIFICATIONS ([~]): {len(modifies)}\n")
    for k, v in modifies.items():
        f.write(f"  ~ {k}: OLD={v['old']} --> NEW={v['new']}\n")
    
    f.write(f"\nEXTRA REMOTES (missing locally) ([?]): {len(deletes)}\n")
    for k, v in deletes.items():
        f.write(f"  ? {k}={v} (Will remain unchanged unless pruned)\n")

# Write the Rollback Script
with open(rollback_script_path, 'w') as f:
    f.write("#!/usr/bin/env bash\n")
    f.write("# Automated Rollback Script generated on " + "${TIMESTAMP}" + "\n")
    f.write("set -e\n")
    f.write("echo 'Starting environment rollback to backup state...'\n")
    
    # Reverse modifications
    for k, v in modifies.items():
        # Escape quotes for bash safety
        escaped_old_val = v['old'].replace("'", "'\\\\''")
        f.write(f"railway variables set '{k}={escaped_old_val}'\n")
    
    # Delete new additions
    for k in adds.keys():
        f.write(f"railway variables delete '{k}'\n")
        
    f.write("echo 'Rollback execution completed successfully.'\n")

# Make rollback script executable
os.chmod(rollback_script_path, 0o755)

# Display Summary
print(f"\n{CYAN}--- VARIABLE COMPARISON DIFF REPORT ---{NC}")
if not adds and not modifies:
    print(f"{GREEN}✓ No changes detected! Local .env_production matches Railway state perfectly.{NC}")
    sys.exit(0)

if adds:
    print(f"\n{GREEN}[+] ADDITIONS ({len(adds)}):{NC}")
    for k, v in adds.items():
        print(f"  {GREEN}+ {k}={v}{NC}")

if modifies:
    print(f"\n{YELLOW}[~] MODIFICATIONS ({len(modifies)}):{NC}")
    for k, v in modifies.items():
        print(f"  {YELLOW}~ {k}:{NC} {RED}{v['old']}{NC} --> {GREEN}{v['new']}{NC}")

if deletes:
    print(f"\n{BLUE}[?] REMOTE-ONLY (Will remain untouched) ({len(deletes)}):{NC}")
    for k in deletes.keys():
        print(f"  ? {k}")

print(f"\n{CYAN}---------------------------------------{NC}")
print(f"📝 Full Diff Report saved to: {diff_report_path}")
print(f"🛠️  Emergency Rollback Script generated at: {rollback_script_path}")
print(f"{CYAN}---------------------------------------{NC}")
EOF

echo -e "\n${BLUE}[Step 3/5] Requesting Release Approval...${NC}"
echo -e "${YELLOW}⚠️  WARNING: Applying these changes will overwrite values in the PRODUCTION database/auth services!${NC}"

# Ask for explicit confirmation before making changes
read -p "Do you approve this release plan and want to apply changes to Railway? (Type 'yes' to proceed, 'no' to abort): " APPROVAL

if [ "$APPROVAL" != "yes" ]; then
    echo -e "${RED}Release aborted by user. Zero changes were applied to Railway.${NC}"
    exit 0
fi

echo -e "\n${GREEN}✓ Release Approved! Setting variables on Railway...${NC}"
echo -e "${BLUE}[Step 4/5] Syncing variables with Railway...${NC}"

# Parse .env_production and set variables in Railway
while IFS= read -r line || [ -n "$line" ]; do
    # Skip comments and empty lines
    if [[ -n "$line" && ! "$line" =~ ^# ]]; then
        # Set variable in active Railway project
        echo -e "Uploading: ${GREEN}${line%%=*}${NC}"
        railway variables set "$line" > /dev/null
    fi
done < .env_production

echo -e "\n${GREEN}✓ Environment variables successfully synced with Railway!${NC}"

echo -e "\n${BLUE}[Step 5/5] Initiating Post-Deployment Verification Check...${NC}"
echo -e "We will stream the live deployment logs in a moment."
echo -e "You can press ${YELLOW}ctrl+c${NC} at any time to exit the logs stream."
echo -e "Run ${YELLOW}railway logs${NC} manually if you want to inspect them further."
echo -e "Run ${YELLOW}logs/rollback_railway_config_${TIMESTAMP}.sh${NC} if you need to rollback immediately!"
echo -e "${CYAN}====================================================${NC}"

# Fetch logs
railway logs

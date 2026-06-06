# NEXTLearn Secure Production Deployment Runbook

This document outlines the **hardened, risk-mitigated DevOps workflow** for managing and deploying production environment variables for the NEXTLearn backend on Railway. 

Our variables are managed defensively to ensure zero credential leakage, eliminate human copy-paste errors, and guarantee a rapid recovery path in the event of an emergency.

---

## 🛡️ The 5 Pillars of Secure Variable Deployment

We adhere to strict, enterprise-grade SRE (Site Reliability Engineering) standards when updating production configurations:

### 1. The Immutable Backup-First Mandate
Before any change is applied to the production environment, the active configurations are fetched in their entirety from Railway and saved to a secure, timestamped JSON backup file (`logs/railway_backup_<timestamp>.json`). This ensures we possess a flawless, restorable snapshot of the system's state immediately preceding the release.

### 2. Mandatory Dry-Run Diff Analysis
To prevent pushing corrupt, missing, or mistyped keys blindly, our release pipeline always runs a key-by-key comparison dry-run. It generates a colored console diff highlighting:
* 🟢 **Additions (`[+]`)**: New configuration keys being introduced.
* 🟡 **Modifications (`[~]`)**: Existing keys being updated, showing `OLD_VALUE` vs. `NEW_VALUE` side-by-side.
* 🔴 **Remote-Only Warnings (`[?]`)**: Keys that exist on Railway but are omitted in the local configuration (representing active live overrides or legacy clutter).

### 3. Dynamic Emergency Rollback Script
During a production incident, stress-induced manual recovery is highly error-prone. Our pipeline dynamically compiles a completely custom, ready-to-run rollback shell script (`logs/rollback_railway_config_<timestamp>.sh`) *before* applying any changes. This rollback script contains the exact reverse API calls to instantly restore the environment to its original state.

### 4. Interactive Human-in-the-Loop Gate
All configuration updates are gated behind an interactive approval step. The operator must actively review the printed Diff Report, acknowledge the generated Rollback Script path, and explicitly type `yes` to proceed. Any other input aborts the release cleanly with zero production impact.

### 5. Secure, Git-Ignored Credential Syncing
To prevent critical security leaks, production credentials must never be hardcoded in application code or committed to git. All production keys reside exclusively in a local `.env_production` file which is strictly ignored by `.gitignore`. The release script reads this file line-by-line and pushes variables directly to Railway's private environment.

---

## 🚀 Step-by-Step Production Release Runbook

### Prerequisites
1. Ensure the **Railway CLI** is installed on your local machine:
   ```bash
   npm install -g @railway/cli
   ```
2. Authenticate and link your local repository to your Railway project:
   ```bash
   railway login
   railway link
   ```

### Step 1: Prepare the Production Environment File
Create a `.env_production` file at your project root using the following secure template (ensure actual keys are populated, and do **not** check this file into Git):
```env
# Neo4j Database Configuration (Production)
NEO4J_URI=bolt://your-neo4j-host-on-railway:7687
NEO4J_USERNAME=neo4j
NEO4J_PASSWORD=your-production-neo4j-password

# OAuth2 / Security Settings (Production Vercel Frontend)
OAUTH2_CLIENT_ID=next-client
OAUTH2_CLIENT_SECRET=your-production-client-secret
OAUTH2_REDIRECT_URI=https://your-frontend-domain.vercel.app/api/auth/callback/next-learn
OAUTH2_POST_LOGOUT_REDIRECT_URI=https://your-frontend-domain.vercel.app/
JWT_ISSUER=https://your-backend-domain.up.railway.app
CORS_ALLOWED_ORIGINS=https://your-frontend-domain.vercel.app
SPRING_PROFILES_ACTIVE=prod

# JWT/JWK Persistent Key Settings (Required for production)
JWT_PRIVATE_KEY_PEM=your-production-pkcs8-private-key-pem
JWT_PUBLIC_KEY_PEM=your-production-x509-public-key-pem
JWT_KEY_ID=daglearn-production-jwt-key

# Database Seeding Overrides
FORCE_RESEED=false
```

### Step 2: Execute the Automated Release Script
Run our custom safety-orchestrated configuration script:
```bash
./manage-railway-config.sh
```

### Step 3: Review the Diff & Approve
* Inspect the printed terminal report carefully.
* Ensure all additions and modifications align exactly with your release plan.
* Type **`yes`** to authorize the configuration synchronization and trigger the Railway deployment.

### Step 4: Verify Live Logs
Once approved, the script automatically begins streaming the live Railway deployment logs. Verify the following startup lines:
* **Database Safety**: Confirm the seeder skips destructive wiping by logging:
  ```text
  AP CSA Knowledge Graph already exists in Neo4j. Skipping database seeding to prevent destructive overwrites.
  ```
* **JWK Persistent Keys**: Confirm persistent keys are successfully loaded from your environment:
  ```text
  Using persistent RSA keys from environment variables with Key ID: <your-key-id>
  ```
* **CORS Settings**: Confirm Tomcat and Spring Security boot up on port `8080` without errors.

---

## 🚨 Emergency Rollback Runbook

In the event of a configuration mismatch, authentication failure, or deployment disruption, you can instantly roll back your production variables:

1. Locate the emergency rollback script path printed in Step 2 of the deployment (e.g., `logs/rollback_railway_config_YYYYMMDD_HHMMSS.sh`).
2. Run the script from the root directory:
   ```bash
   ./logs/rollback_railway_config_YYYYMMDD_HHMMSS.sh
   ```
3. The script will execute, restore all variables to their exact original pre-deployment values, and automatically trigger a clean, stable redeployment.

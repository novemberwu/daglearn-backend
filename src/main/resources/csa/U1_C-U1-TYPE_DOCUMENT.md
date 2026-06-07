### 1. Java is Strongly Typed
Java enforces strict type safety before execution:
1. Before any variables can be used, they **must be declared** with a specific type.
2. Java variables **must have a specific type** that **can never change** during execution.
3. Types are checked and verified by the compiler **before** the program runs (unlike Python, where types are verified during execution).

### 2. Classifications of Types
In Java, types are divided into:
*   **Primitive Types**: Directly store raw values (e.g., `int`, `double`, `boolean`, `char`).
*   **Reference Types**: Store a **reference (memory address)** pointing to an object in memory (e.g., `String`).

### 3. Built-in Data Types
| Type | Set of Values | Common Operations | Sample Value |
| :--- | :--- | :--- | :--- |
| **int** | Integers | `+ - * / %` | `1`, `2`, `-34353` |
| **double** | Floating-point numbers | `+ - * / %` | `3.14` |
| **boolean** | Boolean values | `&& \|\| !` | `true`, `false` |
| **char** | Characters | None | `'A'`, `'1'`, `'\n'` |
| **String** | Sequences of characters | `+` (concatenation) | `"Hello World"` |

### 4. The "Remote Control" Analogy
A reference type variable acts like a **remote control** pointing to a television set (the actual object in the heap):
*   If two reference variables are assigned to each other (e.g., `ref1 = ref2;`), they point to the exact same object in memory.
*   Comparing them with `==` compares the **memory addresses** (addresses of the televisions), not their contents.

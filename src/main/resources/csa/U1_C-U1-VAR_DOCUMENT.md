### 1. What is a Variable?
A **variable** is a "named container" in memory that holds a value. For example:
```java
q = 100 - q;
```
This statement executes three operations sequentially:
1. **Read** the current value stored in the container `q`.
2. **Subtract** that value from `100`.
3. **Move** the final result back into the container `q` (overwriting the old value).

### 2. Key Vocabulary
*   **Declaration Statement**: Associates a variable name with a data type (e.g., `int age;`).
*   **Assignment Statement**: Stores a value in a variable (e.g., `age = 16;`).
*   **Initialization**: Combining declaration and assignment in one line (e.g., `int year = 365;`).
*   **Literal**: A raw programming-language representation of a value (e.g., `365` is an integer literal, `3.14` is a double literal, `"Hello"` is a String literal).

### 3. Variable Scope
The **scope** of a variable is the region of the source code where that variable is "visible" and can be accessed.
*   **Compile-time rule**: Scope is checked by the compiler. Using a variable outside its scope triggers a syntax error.
*   Variables can have identical names **only if** their scopes do not overlap.

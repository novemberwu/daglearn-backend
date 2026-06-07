### 1. What is a Variable?
A **variable** is a "named container" in memory that holds a value.
*   Variables can store different data types: `int`, `double`, `boolean`, etc.
*   The programmer defines the names of variables. By convention, variable names in Java are **case-sensitive** and usually start with a **lowercase letter** (using camelCase for multi-word names).

For example, consider the assignment statement:
```java
q = 100 - q;
```
This statement executes three operations sequentially:
1. **Read**: Retrieve the current value stored in the container `q`.
2. **Subtract**: Calculate the value of `100 - q`.
3. **Move**: Store the final result back into the container `q`, overwriting the previous value.

---

### 2. Key Vocabulary & Syntax
*   **Declaration Statement**: Associates a variable name with a specific data type (e.g., `int age;` or `double dollarsAndCents;`).
*   **Assignment Statement**: Stores a value in an already declared variable using the `=` operator (e.g., `age = 16;`).
*   **Initialization**: Combining declaration and assignment into a single line (e.g., `int year = 365;`).
*   **Literal**: A raw, hardcoded representation of a value in your source code (e.g., `365` is an integer literal, `3.14` is a double literal, and `"Hello"` is a String literal).

#### Syntax Pitfalls & Common Mistakes
*   **Invalid Declaration syntax**: You cannot declare variables of different types in a single statement separated by commas.
    *   ❌ `int hours, double pay;` // **Syntax Error**
    *   Instead, write them on separate lines:
        *   `int hours;`
        *   `double pay;`
*   **Valid Chained Initialization**: You can declare and initialize multiple variables of the same type in one line.
    *   ✅ `int year = 365, leapYear = year + 1;` // **Valid**

---

### 3. Variable Scope
The **scope** of a variable is the specific region of the source code where that variable is "visible" and can be accessed.
*   **Compile-time rule**: Variable scope is strictly checked by the compiler. Attempting to read or modify a variable outside its defined scope will result in a compile-time syntax error.
*   **Naming Overlaps**: Two variables in the same program can share the exact same name **only if** their scopes do not overlap.

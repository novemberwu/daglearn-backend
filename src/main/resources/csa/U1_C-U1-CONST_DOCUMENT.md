### 1. Symbolic Constants in Java
Symbolic constants are variables declared with the **`final`** keyword whose value cannot change once initialized.
```java
final double PI = 3.14159;
```
*   By convention, constants are written in **ALL_CAPS** with underscores separating words.

### 2. Why Use Symbolic Constants?
1. **Maintainability**: Easy to modify values centrally across the program.
2. **Readability**: Self-documenting code. Reading `PI` is far clearer than mystery numbers like `3.14159`.
3. **Compile-time Check**: The compiler prevents any attempt to overwrite constant values, providing stronger security.
4. **Additional Type Checking**: Extra compiler safeguards.

Consider the following statement:
```java
final int MAX_ATTEMPTS = 5;
```
What is the primary benefit of declaring `MAX_ATTEMPTS` using the `final` keyword?

A) It allows `MAX_ATTEMPTS` to change its value dynamically to adapt to user attempts during program execution.
B) It tells the JVM to allocate the variable directly on the Heap as a dynamic reference type.
C) It prevents the variable's value from being reassigned, making the codebase more secure, maintainable, and compiler-verified.
D) It bypasses Java's strict compile-time type safety checks.

---
ANSWER KEY & EXPLANATION
Correct Answer: C
Explanation: The `final` keyword in Java declares a constant. Once initialized, its value cannot be changed. This guarantees that the compiler will prevent any reassignment, making code safer, less error-prone, and easier to read/maintain.
Distractor Analysis:
A) Incorrect. Constants cannot be updated or reassigned after initialization.
B) Incorrect. `final` does not alter where the variable is stored (stack vs heap); it only restricts reassignment.
D) Incorrect. Declaring a variable `final` reinforces type safety rather than bypassing it.

A student declares a variable `double x;` in Java. Later in the program, they write the assignment statement `x = "3.14";`. Which of the following best describes what occurs?

A) The code compiles successfully and automatically converts the string `"3.14"` into the double value `3.14` at runtime.
B) A compiler syntax error occurs because Java is strongly typed, meaning all variables must have compatible types verified at compile time.
C) A runtime `NullPointerException` is thrown because the String literal is not initialized on the heap.
D) The variable `x` dynamically switches its type from `double` to `String` to accommodate the value.

---
ANSWER KEY & EXPLANATION
Correct Answer: B
Explanation: Java is a strongly typed language. All variables must be declared with a specific type, and their type can never change. Assigning a `String` literal to a `double` variable is a type mismatch, which is caught and flagged by the compiler before execution.
Distractor Analysis:
A) Incorrect. Java does not implicitly convert a String literal representing a number into a primitive float or double. This requires explicit parsing.
C) Incorrect. Type mismatches are caught at compile time, not runtime, and do not trigger a NullPointerException in this context.
D) Incorrect. This is a misconception from dynamically-typed languages like Python. In Java, variable types are static and immutable once declared.

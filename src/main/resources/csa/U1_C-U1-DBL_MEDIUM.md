Consider the following comparison between two floating-point variables, `a` and `b`:
```java
double a = 5.0 / 3.0;
double b = 1.6666666666666667;
```
Why is the direct comparison statement `a == b` discouraged in Java?

A) Double variables are reference types, meaning `==` compares their stack memory addresses instead of their values.
B) Direct numeric comparisons using `==` will cause a compile-time syntax error for double types.
C) Binary rounding errors can accumulate in floating-point representations, causing mathematically equal calculations to differ slightly and evaluate to `false`.
D) Double comparisons using `==` always trigger an `ArithmeticException` at runtime.

---
ANSWER KEY & EXPLANATION
Correct Answer: C
Explanation: Floating-point numbers (`double`) are stored in binary scientific notation, which cannot represent all decimal fractions exactly. This leads to slight rounding errors. Because `==` performs an exact bitwise check, even a tiny discrepancy in the last decimal place will cause the equality check to fail.
Distractor Analysis:
A) Incorrect. Double is a primitive data type, not a reference type. `==` checks value equality for primitives.
B) Incorrect. `==` is syntactically valid for doubles, but is semantically unreliable.
D) Incorrect. It does not cause a runtime exception; it simply returns an incorrect boolean value.

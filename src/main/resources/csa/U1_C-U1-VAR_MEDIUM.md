Consider the following code segment:
```java
int q = 12;
q = 100 - q;
```
Which of the following is true regarding variable `q` after this code is compiled and executed?

A) `q` retains its original declared value of `12` because primitive variables cannot be updated once initialized.
B) `q` stores the value `88` because the subtraction on the right-hand side is evaluated first, and then the result is assigned back to `q`.
C) A syntax error is reported because `q` is used on both sides of the assignment statement.
D) `q` stores the value `100` because variables always default to the initial literal expression.

---
ANSWER KEY & EXPLANATION
Correct Answer: B
Explanation: In Java, the right-hand side of an assignment statement (`100 - q`) is evaluated first using the current value of `q` (which is `12`), yielding `88`. Then, the assignment operator `=` stores this result back into `q`, replacing the old value.
Distractor Analysis:
A) Incorrect. This is a misconception that primitive variables are final by default. Variables can be reassigned unless declared with `final`.
C) Incorrect. Self-assignment using the variable's current value (e.g., `q = 100 - q;`) is perfectly valid Java syntax.
D) Incorrect. The value is recalculated dynamically based on the expression, not defaulted to the first operand.

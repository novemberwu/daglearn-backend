What is the value of the variable `result` after the following Java code segment is executed?
```java
int result = 3 + 5 / 2 - 5 % 3;
```

A) 3
B) 2
C) 4
D) 1

---
ANSWER KEY & EXPLANATION
Correct Answer: A
Explanation: According to Java operator precedence:
1. The multiplicative operators `/` and `%` are evaluated first from left to right.
   - `5 / 2` performs integer division, dropping the fractional part, resulting in `2`.
   - `5 % 3` calculates the remainder of `5` divided by `3`, resulting in `2`.
2. The additive operators `+` and `-` are then evaluated from left to right.
   - `3 + 2` is `5`.
   - `5 - 2` is `3`.
Therefore, `result` stores `3`.
Distractor Analysis:
B) Incorrect. This would occur if modulo had higher precedence than division, or if integer division rounded up.
C) Incorrect. This would occur if standard arithmetic division was used (`3 + 2.5 - 1.67 = 3.83` rounded up).
D) Incorrect. This represents a miscalculation of the modulo operator (confusing remainder with quotient).

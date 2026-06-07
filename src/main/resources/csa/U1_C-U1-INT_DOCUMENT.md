### 1. Integer Arithmetic & `int` Type
An `int` is a primitive type representing integers:
*   Stored in **32 bits** of memory, holding values between $-2^{31}$ and $2^{31}-1$.
*   Integer computations are mathematically **exact**.

### 2. Operators & Integer Division
*   `/` (Integer Division): **Drops the fractional part** entirely (e.g., `5 / 3` is `1` because `3` goes into `5` exactly once).
*   `%` (Modulo): Computes the remainder (e.g., `5 % 3` is `2`).
*   Division by zero (`1 / 0`) throws a runtime `ArithmeticException`.

### 3. Precedence & Associativity
1. Multiplicative operators (`*`, `/`, `%`) have higher precedence than additive operators (`+`, `-`).
2. Operators with the same precedence are evaluated from left to right (**left-associative**):
   *   `3 - 5 - 2` evaluates as `(3 - 5) - 2 = -4`.

### 4. Java Overflow Trap
Be careful of the Java overflow trap! Exceeding the maximum positive value of a 32-bit signed integer wraps around silently to negative numbers due to **Two's Complement** representation (e.g., `0x7fffffff + 1` becomes `-2147483648`).
*   **Two's Complement calculation**:
    1. Take the absolute value.
    2. Convert it to binary.
    3. Invert all bits (one's complement).
    4. Add 1.
*   *Fun Fact:* In 2014, Psy's "Gangnam Style" video counts neared the 32-bit integer limit, prompting YouTube to upgrade its counter to 64-bit!

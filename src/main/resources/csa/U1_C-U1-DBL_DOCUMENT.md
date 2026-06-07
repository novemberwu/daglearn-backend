### 1. Floating-Point & `double` Type
A `double` represents real numbers.
*   The internal representation is like scientific notation (sign, mantissa, exponent).
*   **Computation on doubles is NOT exact** due to binary representation limits!
    *   `5.0 / 3.0` yields `1.6666666666666667`.
    *   `3.141 + 0.03` yields `3.171`.

### 2. The Danger of `==`
Never use `==` to check if two `double` values are mathematically equal because rounding errors can accumulate, causing mathematically identical expressions to evaluate to `false`.

### 3. The Solution: Epsilon Comparison
Determine if the absolute difference between the two doubles is smaller than a tiny tolerance value (**epsilon**):
```java
double epsilon = 0.00001;
if (Math.abs(a - b) < epsilon) {
    // a and b are close enough to be considered equal
}
```
*Always compare doubles by checking if they are within a small threshold of each other!*

### 1. The `String` Reference Type
A `String` is a reference type representing a sequence of characters.
*   `+` represents **concatenation** (e.g., `"1" + "2" + "1"` becomes `"121"`).
*   Strings are created as literals or via constructors (e.g., `new String("Hello")`).

### 2. String Memory Layout
*   **Stack**: Stores reference variables (which hold memory addresses pointing to objects on the heap).
*   **Heap**: Stores actual objects created via `new`.
*   **String Pool**: A special memory area within the heap where String literals are cached to optimize space.

### 3. Comparison
*   `==` compares **memory addresses** (referential equality).
*   `.equals()` compares the actual **character content** (structural equality).
```java
String s1 = "hello"; // lives in String Pool
String s2 = new String("hello"); // lives in Heap
System.out.println(s1 == s2);      // false (different memory addresses)
System.out.println(s1.equals(s2)); // true (same character content)
```

### 4. Immutability
String objects are **immutable**: their values cannot be changed once created.
*   Methods called on a String (like `.toUpperCase()`) do not modify the original String object. Instead, they return a **brand-new** String object.

### 5. Empty vs. Null
*   `""` (Empty String): An actual string object exists with length `0`.
*   `null`: Uninitialized reference. Calling `.length()` on `null` will throw a `NullPointerException` (NPE).

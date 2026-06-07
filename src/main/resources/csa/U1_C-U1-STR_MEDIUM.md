Consider the following Java code segment:
```java
String s1 = "AP";
String s2 = "CSA";
String s3 = s1 + s2;
```
Which of the following statements is true regarding memory and behavior in this segment?

A) Evaluating `s1 + s2` modifies the existing `"AP"` string literal inside the String Pool.
B) Because String objects are immutable, the concatenation operation creates a brand-new String object `"APCSA"` in memory.
C) The expression `s3.equals("APCSA")` will evaluate to `false` because they occupy different reference stacks.
D) `s1` and `s2` are stored directly on the execution stack, bypassing the heap String Pool.

---
ANSWER KEY & EXPLANATION
Correct Answer: B
Explanation: String objects in Java are immutable. Any operation that appears to modify a String (like concatenation with `+`) actually constructs and returns a completely new String object in memory, leaving the original Strings unchanged.
Distractor Analysis:
A) Incorrect. The literal `"AP"` in the String Pool can never be altered because of string immutability.
C) Incorrect. The `.equals()` method compares the character content of the strings. Since the content is `"APCSA"`, it will return `true`.
D) Incorrect. String is a reference type. The String variables live on the stack but reference objects residing in the String Pool or Heap.

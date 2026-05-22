// 1. Create Topics
MERGE (arrays:Topic {id: 'T-1'})
SET arrays.title = 'Arrays', 
    arrays.description = 'Contiguous memory collection of elements.';

MERGE (linkedLists:Topic {id: 'T-2'})
SET linkedLists.title = 'Linked Lists', 
    linkedLists.description = 'Linear collection of data elements whose order is not given by their physical placement in memory.',
    linkedLists.requiredProficiencyScore = 80;

// 2. Create Concepts for Linked Lists
MERGE (nodeStructure:Concept {id: 'C-1'})
SET nodeStructure.name = 'Node Structure', 
    nodeStructure.description = 'Understanding the data field and the next pointer.';

MERGE (traversal:Concept {id: 'C-2'})
SET traversal.name = 'Traversal', 
    traversal.description = 'The logic of visiting nodes until reaching null.';

// 3. Create Resources (MCQs)
MERGE (mcq1:Resource:MCQ {id: 'R-1'})
SET mcq1.type = 'MCQ', 
    mcq1.content = 'What happens if you lose the head pointer?', 
    mcq1.options = ['The entire list becomes unreachable (Memory Leak)', 'Only the head is lost', 'Nothing happens', 'The next pointer is updated'],
    mcq1.correctAnswer = 'The entire list becomes unreachable (Memory Leak)';

MERGE (mcq2:Resource:MCQ {id: 'R-2'})
SET mcq2.type = 'MCQ', 
    mcq2.content = 'What is the time complexity to access the i-th element in a Singly Linked List?', 
    mcq2.options = ['O(1)', 'O(log n)', 'O(n)', 'O(n^2)'],
    mcq2.correctAnswer = 'O(n)';

// 4. Build Relationships
MATCH (t2:Topic {id: 'T-2'}), (c1:Concept {id: 'C-1'}) MERGE (t2)-[:CONTAINS]->(c1);
MATCH (t2:Topic {id: 'T-2'}), (c2:Concept {id: 'C-2'}) MERGE (t2)-[:CONTAINS]->(c2);
MATCH (c1:Concept {id: 'C-1'}), (r1:Resource {id: 'R-1'}) MERGE (c1)-[:ASSESSED_BY]->(r1);
MATCH (c2:Concept {id: 'C-2'}), (r2:Resource {id: 'R-2'}) MERGE (c2)-[:ASSESSED_BY]->(r2);
MATCH (t2:Topic {id: 'T-2'}), (t1:Topic {id: 'T-1'}) MERGE (t2)-[:REQUIRES]->(t1);

# NEXTLearn Knowledge Graph Data Model

This document outlines the Directed Acyclic Graph (DAG) structure used to represent knowledge in the NEXTLearn platform using a Labeled Property Graph (LPG) model in Neo4j.

## 1. Hierarchy Overview

The knowledge graph is organized into three distinct layers to enable granular tracking of user proficiency and prerequisite-based unlocking.

| Layer | Node Label | Description | Primary Relationships |
| :--- | :--- | :--- | :--- |
| **Topic** | `:Topic` | High-level educational module (e.g., Iteration, Variables & Data Types). | `REQUIRES` (to other Topics), `CONTAINS` (to Concepts) |
| **Concept** | `:Concept` | Atomic unit of theoretical knowledge. | `ASSESSED_BY` (to Resources) |
| **Resource** | `:Resource` | Learning material or assessment item. Polymorphic base node for both explanations and practice questions. | N/A (Targets of Concept relationships) |

---

## 2. Full-Picture Architecture Diagram

```text
=========================================================================================
                                STRUCTURAL KNOWLEDGE GRAPH
=========================================================================================

     +-------------------------------+
     |            Course             |
     +-------------------------------+
     | - id: String (ID)             |
     | - name: String                |
     | - subject: String             |
     +-------------------------------+
                     |
                     |  -[:CONTAINS]->  (Outgoing)
                     v
     +-------------------------------+
     |             Topic             | <=== -[:REQUIRES]=== (Prerequisite Directed Loop)
     +-------------------------------+
     | - id: String (ID)             |
     | - title: String               |
     | - description: String         |
     | - requiredProficiencyScore:int|
     +-------------------------------+
                     |
                     |  -[:CONTAINS]->  (Outgoing)
                     v
     +-------------------------------+
     |            Concept            |
     +-------------------------------+
     | - id: String (ID)             |
     | - name: String                |
     | - description: String         |
     +-------------------------------+
                     |
                     |  -[:ASSESSED_BY]->  (Outgoing)
                     v
     +-------------------------------------------------------------+
     |                 Resource (Abstract Node)                    |
     +-------------------------------------------------------------+
     | - id: String (ID)                                           |
     | - type: String (e.g., "MCQ", "DOCUMENT", "VIDEO")           |
     | - content: String (Markdown text or prompt content)         |
     +-------------------------------------------------------------+
                     ^                             ^
                     | (Inheritance)               | (Inheritance)
                     |                             |
      +-----------------------------+       +-----------------------------+
      |       McqResource           |       |      DocumentResource       |
      |     (Node Label: MCQ)       |       |   (Node Label: DOCUMENT)    |
      +-----------------------------+       +-----------------------------+
      | - options: List<String>     |       | [Inherits all fields from   |
      | - correctAnswer: String     |       |  Resource with no additions]|
      +-----------------------------+       +-----------------------------+


=========================================================================================
                          USER PERFORMANCE & TRACKING GRAPH
=========================================================================================

     +-------------------------------+
     |             User              |
     +-------------------------------+
     | - id: String (email/ID)       |
     | - username: String            |
     | - email: String               |
     | - password: String (Hashed)   |
     +-------------------------------+
         |             |           |
         |             |           +--------------------------------------------+
         |             |                                                        |
         |             +-------------------------+                              |
         | -[:ATTEMPTED]->                       | -[:UNDERSTANDS]->            | -[:MASTERED]->
         |                                       |                              |
         v [Relationship Properties]             v [Relationship Properties]    v [Relationship Properties]
   +---------------------------+           +---------------------------+  +---------------------------+
   |      ResourceAttempt      |           |     ConceptProficiency    |  |      TopicProficiency     |
   +---------------------------+           +---------------------------+  +---------------------------+
   | - id: Long (Generated)    |           | - id: Long (Generated)    |  | - id: Long (Generated)    |
   | - score: int (0 or 1)     |           | - percentage: double      |  +---------------------------+
   | - attemptDate: Instant    |           +---------------------------+                |
   +---------------------------+                         |                              |
                 |                                       |                              |
                 | (Points to TargetNode)                | (Points to TargetNode)       | (Points to TargetNode)
                 v                                       v                              v
   +---------------------------+           +---------------------------+  +---------------------------+
   |    Resource (Subclass)    |           |          Concept          |  |           Topic           |
   +---------------------------+           +---------------------------+  +---------------------------+
```

---

## 3. Node Definitions & Properties

### User Node (`:User`)
Represents a student in the system.
- `id`: String (Primary Key, typically email)
- `username`: String
- `email`: String
- `password`: String (BCrypt hashed)

### Course Node (`:Course`)
Represents a course in the system.
- `id`: String (key)
- `name`: String
- `subject`: String 

### Topic Node (`:Topic`)
Represents a module in the curriculum (e.g., AP CSA Units).
- `id`: String (e.g., "U1")
- `title`: String
- `description`: String
- `requiredProficiencyScore`: Integer (Threshold to unlock dependent topics, default: 80)

### Concept Node (`:Concept`)
A granular piece of knowledge within a topic.
- `id`: String (e.g., "C-1")
- `name`: String
- `description`: String

### Resource Node (`:Resource`)
Abstract base node for all learning and validation materials.
- `id`: String
- `type`: String (e.g., "MCQ", "DOCUMENT")
- `content`: String

#### MCQ Resource (`:MCQ` extends `:Resource`)
Represents practice questions used for active assessment.
- `options`: List<String>
- `correctAnswer`: String

#### Document Resource (`:DOCUMENT` extends `:Resource`)
Represents instructional readings, explanations, and code demonstrations.
- Inherits all properties from the abstract `:Resource` node with no additional fields.

---

## 4. Relationship Specifications

### Structural Relationships
- `(Course)-[:CONTAINS]->(Topic)`: Defines the relationship between courses and topics. A topic can belong to multiple courses. For example, arrays belong to both AP CSA and Data Structures.
- `(Topic)-[:REQUIRES]->(Topic)`: Defines the prerequisite DAG. Direction: Dependent -> Required.
- `(Topic)-[:CONTAINS]->(Concept)`: Links a module to its constituent concepts.
- `(Concept)-[:ASSESSED_BY]->(Resource)`: Links a concept to its learning (DOCUMENT) and validation (MCQ) materials.

### User Proficiency Relationships (Stateful)
- `(User)-[:ATTEMPTED {score, attemptDate}]->(Resource)`: Tracks individual assessment results (score: 0 or 1).
- `(User)-[:UNDERSTANDS {percentage}]->(Concept)`: Tracks student mastery over a specific concept. If the score rate on a concept's assessment items exceeds 90%, the user understands the concept.
- `(User)-[:MASTERED]->(Topic)`: Established once the user understands all constituent concepts under this topic.

---

## 5. AP CSA Example Flow

### Prerequisite Chain
- `(U7:Topic {title: 'ArrayList'}) -[:REQUIRES]-> (U6:Topic {title: 'Array'})`

### Content Mapping
- `(AP CSA) -[:CONTAINS]-> (U7:Topic)`
- `(U6:Topic) -[:CONTAINS]-> (C1:Concept {name: 'Array Traversal'})`
- `(C1:Concept) -[:ASSESSED_BY]-> (R1:MCQ {content: 'How do you access the last element?'})`
- `(C1:Concept) -[:ASSESSED_BY]-> (R2:DOCUMENT {content: 'An explanation of arrays and boundary conditions...'})`

### Proficiency Roll-up Flow
1. User submits an answer to **R1** -> System creates/updates `[:ATTEMPTED]` relationship.
2. System recalculates `[:UNDERSTANDS]` for **C1** based on all linked resource attempts.
3. System recalculates `[:MASTERED]` for **U6** based on all constituent concepts.
4. If **U6** mastery >= 80%, the system unlocks **U7** (ArrayList) for the user.

---

## 6. Implementation Notes

*   **Recursion Control**: Java models use `@JsonIgnoreProperties` to prevent infinite loops during JSON serialization of graph traversals.
*   **Stateless Auth**: User password handling is managed on the backend; the frontend only receives the user ID and name via JWT sub/claims.
*   **Directionality**: Prerequisite arrows in the visualization point from the "Required" topic to the "Dependent" topic for clarity, while the Neo4j `REQUIRES` relationship is stored as `(Dependent)-[:REQUIRES]->(Required)`.
*   **Rollup Logic**:
    *   **Step 1**: User submits an assessment response.
    *   **Step 2**: Retrieve all attempts of the corresponding resources. Calculate the score using the formula `(total_correct / total_attempts)` and update the `UNDERSTANDS` percentage for that specific concept.
    *   **Step 3**: Retrieve the parent topic of the concept. If the user's proficiency rate across all concepts under this topic is greater than 90%, establish a `MASTERED` relationship to the topic node.
    *   **Step 4**: Once a user masters a topic, the system unlocks any topics that require this topic as a prerequisite.

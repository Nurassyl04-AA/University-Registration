# University Registration — Facade Pattern

## 1. Project Overview

This project demonstrates the **Facade Design Pattern** in a University Registration domain.

The system coordinates three independent subsystems:

- **Subsystem A — Eligibility Checking**
- **Subsystem B — Seat Allocation**
- **Subsystem C — Billing / Registration Record**

The main registration workflow is:

```text
Client
   |
   v
RegistrationFacade
   |
   +----> A: Eligibility Check
   |
   +----> B: Seat Allocation
   |
   +----> C: Billing / Registration Record
```

The client does not communicate directly with the three subsystems. The `RegistrationFacade` provides a simple entry point and coordinates the complete workflow.

The project uses durable CSV persistence so that the application state survives a real restart.

---

# 2. Project Structure

```text
university-registration/
├── pom.xml
├── data/
│   ├── a_checks.csv
│   ├── b_allocations.csv
│   └── c_records.csv
│
└── src/
    ├── main/
    │   └── java/
    │       └── com/
    │           └── university/
    │               ├── Main.java
    │               │
    │               ├── domain/
    │               │   ├── subsystemA/
    │               │   │   ├── A_CheckRepository.java
    │               │   │   ├── A_EligibilityRule.java
    │               │   │   └── A_StudentCheck.java
    │               │   │
    │               │   ├── subsystemB/
    │               │   │   ├── B_AllocationRepository.java
    │               │   │   ├── B_CapacityValidator.java
    │               │   │   └── B_SeatAllocation.java
    │               │   │
    │               │   └── subsystemC/
    │               │       ├── C_BillingCalculator.java
    │               │       ├── C_RecordRepository.java
    │               │       └── C_RegistrationRecord.java
    │               │
    │               └── facade/
    │                   ├── AdminFacade.java
    │                   └── RegistrationFacade.java
    │
    └── test/
        └── java/
            └── com/
                └── university/
                    └── RegistrationFacadeTest.java
```

---

# 3. Subsystems

## 3.1 Subsystem A — Eligibility Checking

Subsystem A checks whether the student can start the registration process.

### `A_StudentCheck`

Represents the eligibility check.

Its status can be:

```text
NEW
CONFIRMED
CANCELLED
```

Important operations:

- `confirm()`
- `cancel()`

The entity is responsible for maintaining its own state and legal status transitions.

### `A_EligibilityRule`

Contains validation logic for student registration input and eligibility.

### `A_CheckRepository`

Responsible for durable persistence of `A_StudentCheck`.

The repository stores the state in:

```text
data/a_checks.csv
```

---

# 3.2 Subsystem B — Seat Allocation

Subsystem B is responsible for allocating a seat for the selected course.

### `B_SeatAllocation`

Represents a seat allocation.

Its status can be:

```text
NEW
ALLOCATED
CANCELLED
```

Important operations:

- `allocate()`
- `cancel()`

### `B_CapacityValidator`

Checks whether seats are available.

If there are not enough seats, the validator throws an exception and the registration workflow fails.

### `B_AllocationRepository`

Responsible for durable persistence of seat allocations.

The repository stores the state in:

```text
data/b_allocations.csv
```

---

# 3.3 Subsystem C — Billing / Registration Record

Subsystem C is responsible for creating the final registration record and calculating the registration fee.

### `C_RegistrationRecord`

Represents the final registration record.

Its status can be:

```text
NEW
CONFIRMED
CANCELLED
```

Important operations:

- `confirm()`
- `cancel()`

### `C_BillingCalculator`

Calculates the registration fee according to the selected course.

### `C_RecordRepository`

Responsible for durable persistence of registration records.

The repository stores the state in:

```text
data/c_records.csv
```

---

# 4. Facade Layer

## 4.1 RegistrationFacade

`RegistrationFacade` is the main entry point for the registration workflow.

It coordinates:

```text
A → B → C
```

The client does not need to know how the three subsystems work internally.

The normal workflow is:

```text
1. Validate student eligibility
2. Persist A
3. Validate seat availability
4. Persist B
5. Calculate registration fee
6. Persist C
7. Registration completed
```

The facade also handles exceptions and cross-subsystem compensation.

---

# 4.2 AdminFacade

`AdminFacade` provides administrative/manual operations.

It delegates manual compensation operations to the `RegistrationFacade`.

The client therefore does not need direct access to the repositories or subsystem entities.

---

# 5. Cross-Subsystem Consistency and Compensation

The system implements two mandatory compensation scenarios.

## 5.1 Scenario 1 — B fails after A committed

The first scenario is:

```text
A SUCCESS
   ↓
A persisted
   ↓
B FAIL
```

For example, a student passes the eligibility check, but no seat is available.

At this point:

```text
A = already committed
B = not committed
```

The `RegistrationFacade` catches the failure and compensates A.

```text
A ✓
↓
B ✗
↓
compensate A
↓
A = CANCELLED
```

The compensation is performed by changing the persisted status rather than simply losing the record.

This keeps the persisted state consistent.

---

## 5.2 Scenario 2 — C fails after A and B committed

The second scenario is:

```text
A SUCCESS
   ↓
B SUCCESS
   ↓
C FAIL
```

At this point both A and B have already committed.

The facade therefore performs a two-step rollback:

```text
C FAIL
   ↓
compensate B
   ↓
compensate A
```

The compensation order is important because it reverses the successful workflow:

```text
Forward:
A → B → C

Compensation:
B → A
```

C does not need to be compensated because it failed before successfully committing its final record.

---

# 6. Additional Failure Case

Besides the two compensation scenarios, the application handles invalid input.

For example:

```text
studentId = ""
```

or another invalid registration value.

The validation is performed before the registration is committed.

The flow is:

```text
Invalid input
     ↓
A validation fails
     ↓
Exception
     ↓
No subsystem has committed
```

Therefore no compensation is required.

This is different from the B and C failure scenarios because there is no previously committed state to reverse.

---

# 7. Durable Persistence

The application does not use a static in-memory list or map as its database.

The three repositories use CSV files:

```text
data/a_checks.csv
data/b_allocations.csv
data/c_records.csv
```

The repository is responsible for converting between Java objects and persisted records.

For example:

```text
Java Object
    ↓
Repository
    ↓
CSV file
```

and after restarting:

```text
CSV file
    ↓
Repository
    ↓
Java Object
```

Therefore the persisted state survives application restart.

---

# 8. Principle of Least Knowledge

## Before — without Facade

Without the Facade, the client would have to know about all three subsystems and their internal classes.

For example, the client would need to work with:

```java
A_EligibilityRule
A_CheckRepository

B_CapacityValidator
B_AllocationRepository

C_BillingCalculator
C_RecordRepository
```

The client would also need to know the correct order:

```text
A → B → C
```

and would need to know what to do if B or C fails.

This creates many dependencies and exposes internal implementation details.

Conceptually:

```text
Main
 ├── A_EligibilityRule
 ├── A_CheckRepository
 ├── B_CapacityValidator
 ├── B_AllocationRepository
 ├── C_BillingCalculator
 └── C_RecordRepository
```

The client knows too much.

---

## After — with Facade

With the Facade Pattern, the client communicates with:

```text
Main
   ↓
RegistrationFacade
```

The facade internally coordinates:

```text
RegistrationFacade
       |
       +---- A
       |
       +---- B
       |
       +---- C
```

The client only needs to know how to call the facade.

For example:

```java
facade.register(...);
```

The client does not need to know:

- how eligibility is checked;
- how seats are allocated;
- how billing is calculated;
- how data is persisted;
- how compensation works.

This follows the **Principle of Least Knowledge** because each client interacts only with the object it directly needs.

---

# 9. Independence of Subsystems

Subsystems A, B, and C are independent.

There are no direct dependencies such as:

```text
A → B
B → C
C → A
```

Instead:

```text
              RegistrationFacade
               /      |      \
              A       B       C
```

The Facade is the coordinator.

This is important because the assignment requires the three subsystems not to know about each other.

---

# 10. Unit Testing

The project contains unit tests for the registration workflow.

The tests are designed according to the F.I.R.S.T. principles.

## Fast

Tests do not require manual interaction or a running server.

## Independent

Each test starts with a clean persisted state and does not depend on another test.

## Repeatable

The persisted test data is cleaned before each test.

## Self-Validating

Tests use assertions such as:

```java
assertEquals(...)
assertNotNull(...)
assertThrows(...)
```

so they automatically determine whether the test passes or fails.

## Timely

Tests are maintained together with the production code they verify.

---

# 11. Compensation Tests

The two compensation depths are first-class test cases.

## B failure test

The test forces B to fail after A has already committed.

Expected persisted state:

```text
A = CANCELLED
B = not committed
C = not committed
```

## C failure test

The test forces C to fail after A and B have committed.

Expected persisted state:

```text
A = CANCELLED
B = CANCELLED
C = not committed
```

This verifies that the facade performs:

```text
B compensation
        ↓
A compensation
```

rather than only compensating one subsystem.

---

# 12. Domain-Specific Test Helper

The tests can use domain-specific helper methods so that the tests read like statements from the University Registration domain.

For example:

```java
registerStudent("S001", "Advanced");
```

and:

```java
assertRegistrationCancelled("S001");
```

Instead of repeating low-level repository operations in every test, these helpers express the business meaning of the test.

This makes the test easier to read and understand.

---

# 13. Persistence Reload Test

A persistence-reload test verifies that the application does not depend on in-memory state.

The idea is:

```text
Create registration
       ↓
Persist state
       ↓
Close / restart application
       ↓
Create repositories again
       ↓
Read persisted state
       ↓
Verify correct state
```

The important point is that the state comes from the CSV persistence layer, not from objects that remained in memory.

---

# 14. End-to-End CLI Demo

The project provides a console application that allows the examiner to interact with the real registration workflow.

The CLI provides options such as:

```text
1. Register student
2. Register and force B failure
3. Register and force C failure
4. Show persisted state
5. Cancel / compensate by registration ID
0. Exit
```

## Normal registration

Option:

```text
1
```

demonstrates:

```text
A → B → C
```

with all three stages succeeding.

## B failure

Option:

```text
2
```

forces:

```text
A ✓
B ✗
```

and demonstrates compensation of A.

## C failure

Option:

```text
3
```

forces:

```text
A ✓
B ✓
C ✗
```

and demonstrates:

```text
B → A
```

compensation.

## Inspect persisted state

Option:

```text
4
```

reads the actual persisted CSV state and displays the current records.

This allows the examiner to see the result of compensation directly in the persistence layer.

---

# 15. How to Reset the Persisted Store

Before a fresh demonstration, remove the generated CSV files from:

```text
data/
```

The repositories recreate the required files when the application starts.

This allows the demonstration to start from a clean state.

---

# 16. UML Class Diagram

The UML diagram shows:

```text
Main
  |
  v
RegistrationFacade
  |
  +---- Subsystem A
  |
  +---- Subsystem B
  |
  +---- Subsystem C

AdminFacade
  |
  v
RegistrationFacade
```

Each subsystem contains its entity, business-logic class, and persistence repository.

The UML diagram matches the final Java implementation.

---

# 17. Facade vs Adapter

The **Facade** and **Adapter** patterns solve different problems.

The Facade Pattern is appropriate for this project because the problem is to simplify and coordinate access to several subsystems.

The University Registration workflow requires:

```text
A → B → C
```

and also requires compensation when a later subsystem fails.

The `RegistrationFacade` hides these details from the client and provides a simpler interface such as:

```java
facade.register(...);
```

An Adapter would solve a different problem. An Adapter is used when an existing class has an interface that is incompatible with what the client expects.

For example, if the client expected:

```text
pay(amount)
```

but an existing system provided:

```text
makePayment(amount, account)
```

an Adapter could translate between these interfaces.

In this project there is no such incompatible-interface problem. The main problem is coordinating several subsystems and hiding their complexity.

Therefore **Facade is the appropriate pattern**.

---

# 18. Design Summary

The main architectural idea is:

```text
                    Client
                      |
                      v
              RegistrationFacade
                 /      |      \
                /       |       \
               v        v        v
              A         B        C
           Eligibility Seats   Billing
              |         |        |
              v         v        v
          Repository Repository Repository
              |         |        |
              v         v        v
             CSV       CSV       CSV
```

The responsibilities are separated:

| Component | Responsibility |
|---|---|
| Entity | Maintains domain state and legal state transitions |
| Rule / Validator | Checks whether an operation is allowed |
| Calculator | Performs calculations |
| Repository | Saves and loads durable data |
| RegistrationFacade | Coordinates A → B → C and compensation |
| AdminFacade | Provides administrative/manual operations |
| Main | Provides the CLI and user interaction |

---

# 19. Defense Scenarios

During the defense, the application should demonstrate:

### Scenario 1 — Successful registration

```text
A ✓
B ✓
C ✓
```

### Scenario 2 — B failure

```text
A ✓
B ✗
↓
Compensate A
```

### Scenario 3 — C failure

```text
A ✓
B ✓
C ✗
↓
Compensate B
↓
Compensate A
```

### Scenario 4 — Invalid input

```text
Invalid input
↓
Validation failure
↓
No committed state
```

### Scenario 5 — Persistence

```text
Persist data
↓
Restart application
↓
Load data from CSV
```

---

# 20. Key Points for Defense

The most important points to explain are:

1. **Facade coordinates the A → B → C workflow.**
2. **Subsystems A, B, and C do not directly depend on each other.**
3. **Repositories provide durable persistence.**
4. **B failure after A commits causes compensation of A.**
5. **C failure after A and B commit causes compensation of B and then A.**
6. **Invalid input is an additional failure case before anything commits.**
7. **The client knows the Facade instead of knowing all subsystem internals.**
8. **The CLI demonstrates the actual workflow and compensation.**
9. **Facade is used for simplification and coordination, while Adapter is used for incompatible interfaces.**
10. **The persisted state can survive an application restart.**
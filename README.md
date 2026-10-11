# University Registration — Facade Pattern (Topic 7)

Domain: University Registration, Variant 7.

Flow:
A Prerequisite / Financial Hold Checker -> B Seat Allocator / Waitlist Manager -> C Billing System / Transcript Service.

The facade exposes one client operation and coordinates compensation:
- B fails after A: compensate A.
- C fails after A+B: compensate B, then A.

Persistence uses separate CSV files in `data/`, so state survives application restart.

## Run
```bash
mvn test
mvn package
java -cp target/classes com.university.Main
```

## Demo
1. Option 1 — successful A->B->C.
2. Option 2 — forces B failure and shows A compensation.
3. Option 3 — forces C failure and shows B then A compensation.
4. Option 4 — reads persisted state from disk.
5. Option 5 — administrative/manual compensation.

## Facade vs Adapter
Facade simplifies access to several existing subsystems and coordinates a workflow.
Adapter changes one interface into another. Here the main problem is orchestration and simplification, so Facade is appropriate.

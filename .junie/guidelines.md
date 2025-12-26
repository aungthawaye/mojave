# Project Development Guidelines

Last updated: 2025-09-14 08:34 local time

This document captures conventions and practices to keep development consistent across the repository.


## Build / Configuration Instructions
- Nothing special is required at this time. Use the standard project build as defined in the repository.


## Testing Information
- Always create tests to cover all business logic and edge cases.
- Integration and unit-style tests in this codebase should follow the naming rule: test class types must end with `IT`.
    - Example: `CreateChartCommandIT`, `ActivateAccountCommandIT`.
- Prefer JUnit 5 (`org.junit.jupiter`) and Spring Test where applicable.
- Keep tests deterministic and independent. Avoid cross-test dependencies.
- When adding new behavior, include corresponding tests in the same module to validate it.


## Development Conventions
- Do not fix `import` statements.
- Use the `static` keyword wherever possible to improve readability and avoid ambiguity.
    - Example:
        - `static final String NAME = "name";`
        - `static final String NAME_COLUMN = "name_column";`
- Use the `final` keyword wherever possible to improve readability and avoid accidental modification.
    - Example:
        - `final String name = "name";`
        - `final String name = this.name;`
- Use the `this` keyword wherever possible for instance member access to improve readability and avoid ambiguity.
    - Example:
        - `this.name = name;`
        - `return this.repository.save(entity);`
- Use the `var` keyword wherever possible for local variable declarations where the initializer makes the type obvious and readability is not harmed.
    - Example:
        - `var saved = this.repository.save(entity);`
        - `var id = saved.getId();`
- Do not use `this` for static and final variables.
    - Example:
        - `public static final String NAME = "name";`'
- Do not use such kind of import `Instant now = java.time.Instant.now();` (FQNs) but use `import` statement instead.
    - Example:
        - `import java.time.Instant;`
        - `Instant now = Instant.now();`
- Upon generating the DDL for model/entities, follow these conventions:
    - Use the model class's index or unique constraint name as is.
    - Use this pattern and format for generating foreign key constraints and its related Index: 
      - for foreign key constraint `<table primary>_<table foreign>_FK`    
      - for foreign key constraint index `<table primary>_<table foreign>_FK_IDX`    
- Use the `//` comment style for single-line comments.
    - Example: `// This is a single-line comment.`
- Use the `/* */` comment style for multi-line comments.
    - Example: `/* This is a multi-line comment. */`
- Use the `/** */` comment style for Javadoc comments.
    - Example: `/** This is a Javadoc comment. */`
- Use the `// NOSONAR` comment style to suppress SonarQube warnings.
    - Example: `// NOSONAR`
- Organize code lines by similar logic group or nature, and insert a blank line between groups.
    - Within methods, cluster related statements together and separate groups with a blank line.
    - Example ordering inside a method:
        - Validate inputs
        - Prepare/transform data and printing the logs related to that data
        - Execute the core operation and printing the logs related to that operation
        - Persist/IO
        - Map/return result
        - Leave a blank line between these groups.
        - In tests, cluster related assertions together and separate groups with a blank line.
            - Example ordering inside a test:
                - Arrange
                - Act
                - Assert

- General style tips:
    - Keep methods focused; extract helper methods when a method grows too large.
    - Favor constructor or explicit setter injection for dependencies in Spring components.
    - Log at appropriate levels; avoid excessive debug logs in hot paths.


## Notes
- If you introduce new modules or subprojects, update this document with any additional setup or testing nuances.
- If any of these rules conflict with a specific module’s requirements, document the exception in that module and reference it here.

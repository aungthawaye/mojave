# Mojave Technical Documentation

Developer-focused documentation covering architecture, implementation details, and integration guides for the Mojave instant payment switch.

## Table of Contents

### 1. Architecture

System architecture, design patterns, and infrastructure components.

- [High-Level Architecture](01-architecture/high-level-architecture.md) - Complete system overview with 83 modules
- [Module Structure](01-architecture/module-structure.md) - Organization of 7 module categories
- [Bounded Contexts](01-architecture/bounded-contexts.md) - DDD context boundaries and relationships
- [Hexagonal Architecture](01-architecture/hexagonal-architecture.md) - Port and adapters pattern

### 2. Core Modules Deep Dive

Detailed technical documentation for each domain module.

- [Participant Module](02-core-modules/participant-module.md) - FSP, Hub, Oracle, SSP management
- [Accounting Module](02-core-modules/accounting-module.md) - Double-entry ledger implementation
- [Wallet Module](02-core-modules/wallet-module.md) - Position and balance management
- [Settlement Module](02-core-modules/settlement-module.md) - Settlement definitions and provider matching
- [Transaction Module](02-core-modules/transaction-module.md) - Transaction lifecycle tracking

### 3. Critical Flows

End-to-end processing sequences with detailed explanations.

- [Transfer Flow](03-flows/transfer-flow.md) - Complete payment transfer processing
- [Quote Flow](03-flows/quote-flow.md) - Quote request and response handling
- [Settlement Flow](03-flows/settlement-flow.md) - Settlement provider matching and lifecycle
- [Position Management Flow](03-flows/position-management-flow.md) - Reserve, commit, rollback operations

### 4. FSPIOP Rail Implementation

Protocol-specific implementation details.

- [FSPIOP Rail Overview](04-rails/fspiop-rail-overview.md) - Rail architecture and components
- [FSPIOP Transfer Processing](04-rails/fspiop-transfer-processing.md) - Transfer handler implementation

## Quick Start for Developers

### Setting Up Development Environment
1. Prerequisites: Java 25, Maven 3.9+, Docker
2. Clone repository
3. Start infrastructure: `docker-compose up` (MySQL, Kafka, Redis)
4. Build project: `mvn clean install`
5. Run services individually or use mono module

### Understanding the Codebase
1. Start with [High-Level Architecture](01-architecture/high-level-architecture.md)
2. Understand [Module Structure](01-architecture/module-structure.md)
3. Review [Hexagonal Architecture](01-architecture/hexagonal-architecture.md) pattern
4. Study [Transfer Flow](03-flows/transfer-flow.md) as a complete example

### Making Your First Change
1. Identify the module (Participant, Accounting, Wallet, Settlement, Transaction)
2. Review module documentation in Section 2
3. Follow hexagonal architecture layers:
   - contract/ for API changes
   - domain/ for business logic
   - admin/intercom for REST APIs
   - producer/consumer for events
4. Update relevant flow documentation

## Architecture Principles

### Domain-Driven Design (DDD)
- Clear bounded contexts per business domain
- Rich domain models with behavior
- Aggregate patterns with proper boundaries
- Repository pattern for data access
- Domain events for cross-context communication

### Hexagonal Architecture
Each module follows the port and adapters pattern:
```
Module/
├── contract/         # Inbound port (API contracts)
├── domain/           # Core business logic
├── admin/            # Adapter (Admin REST API)
├── intercom/         # Adapter (Service-to-service)
├── producer/         # Adapter (Kafka publishers)
├── consumer/         # Adapter (Kafka listeners)
└── store/            # Adapter (Caching layer)
```

### CQRS Pattern
- Commands modify state
- Queries retrieve data
- Clear interface segregation
- Optimized read models via caching

### Event-Driven Architecture
- Kafka for asynchronous messaging
- At-least-once delivery semantics
- Event sourcing for transaction audit
- Manual acknowledgment for reliability

## Technology Stack Details

### Core Framework
- **Java 25**: Modern Java features, records, pattern matching
- **Spring Boot 4.0.2**: Dependency injection, configuration management
- **Maven**: Multi-module build system (83 modules)

### Data Persistence
- **MySQL**: Primary data store with read/write splitting
- **HikariCP**: High-performance connection pooling
- **Flyway**: Database migration and versioning
- **JPA/Hibernate**: ORM with custom types

### Messaging and Caching
- **Apache Kafka**: Event streaming and cross-context messaging
- **Redis (Redisson)**: Multi-tier distributed caching
- **Jackson**: JSON serialization/deserialization

### Security and Operations
- **HashiCorp Vault**: Secrets management
- **JWS Signatures**: FSPIOP request authentication
- **Log4j**: Structured logging

## Module Categories

### Component Layer (Infrastructure)
Cross-cutting infrastructure concerns:
- flyway: Database migrations
- jpa: Persistence with read/write splitting
- kafka: Message broker configuration
- redis: Caching with Redisson
- vault: Secrets management
- misc: Utilities (Snowflake ID, crypto, DDD helpers)
- web: HTTP/REST utilities

### Scheme Layer (Protocol Definitions)
Protocol specifications and generated models:
- fspiop: FSPIOP v2.0 OpenAPI-generated models

### Core Layer (Domain Contexts)
Business domain bounded contexts:
- participant: FSP/Hub/Oracle/SSP management
- accounting: Double-entry ledger
- transaction: Transaction lifecycle
- wallet: Position/balance management
- settlement: Settlement definitions and records
- common: Shared data types

### Provider Layer (Storage Implementations)
Data access implementations:
- ledger/mysql: MySQL ledger with stored procedures
- forex: Foreign exchange rates
- settlement: Settlement persistence

### Operation Layer (Administrative Use Cases)
Cross-cutting operational workflows:
- usecase: Administrative operations (e.g., OnboardFsp)

### Connector Layer (External Integrations)
Integration with external systems:
- adapter: Protocol adapters
- gateway: API gateway
- sample: Sample connector implementation

### Rail Layer (Protocol Implementations)
Protocol-specific services:
- fspiop: FSPIOP v2.0 rail implementation
  - bootstrap: Controllers and security
  - transfer: Transfer processing
  - quoting: Quote handling
  - lookup: Party lookup

## Port Allocation

Services are separated by concern with distinct ports:

| Module | Admin | Intercom | Service |
|--------|-------|----------|---------|
| Participant | 4101 | 4102 | - |
| Accounting | 4201 | 4202 | - |
| Transaction | 4301 | 4302 | - |
| Settlement | 4401 | 4402 | - |
| Wallet | 4801 | 4802 | 4803 |
| FSPIOP Lookup | - | - | 4503 |
| FSPIOP Quoting | - | - | 4603 |
| FSPIOP Transfer | - | - | 4703 |

## Key Implementation Patterns

### Strongly-Typed Identifiers
All entities use custom ID types extending `EntityId<Long>`:
```java
record AccountId(Long value) extends EntityId<Long> {}
record FspId(Long value) extends EntityId<Long> {}
record TransactionId(Long value) extends EntityId<Long> {}
```

### Command Pattern
Commands encapsulate operations with clear input/output:
```java
interface PostLedgerFlowCommand {
    Output execute(Input input);
    record Input(...) {}
    record Output(...) {}
}
```

### Repository with Specifications
Composable query filters for flexible data access:
```java
repository.findAll(
    where(accountCode(code))
    .and(currency(Currency.USD))
    .and(isActive())
)
```

### Validation by Construction
Use `Objects.requireNonNull()` at construction to ensure invariants:
```java
public FilterItem(FilterGroup filterGroup, FspId fspId) {
    this.filterGroup = Objects.requireNonNull(filterGroup);
    this.fspId = Objects.requireNonNull(fspId);
}
```

### Multi-Tier Caching
Redis + Local + Timer caching strategy:
```java
@CachePut(value = "account", key = "#result.accountId")
Account createAccount(...) { ... }

@Cacheable(value = "account")
Account findById(AccountId id) { ... }
```

## Database Schema

Key tables by module:

**Participant Module:**
- fsp, fsp_currency, fsp_endpoint
- hub, hub_currency
- oracle, ssp, ssp_currency

**Accounting Module:**
- chart, chart_entry
- account
- flow_definition, posting_definition
- ledger, ledger_movement

**Wallet Module:**
- balance, balance_update
- position, position_update

**Settlement Module:**
- settlement_definition
- filter_group, filter_item
- settlement_record

**Transaction Module:**
- transaction, transaction_step

## Critical Source Files

### Transfer Processing
`modules/rail/fspiop/transfer/src/main/java/org/mojave/rail/fspiop/transfer/domain/command/PostTransfersCommandHandler.java`

### Accounting Core
`modules/core/accounting/contract/src/main/java/org/mojave/core/accounting/contract/command/ledger/PostLedgerFlowCommand.java`

### Wallet Core
`modules/core/wallet/contract/src/main/java/org/mojave/core/wallet/contract/command/position/ReservePositionCommand.java`

### Settlement Core
`modules/core/settlement/domain/src/main/java/org/mojave/core/settlement/domain/model/SettlementDefinition.java`

### Participant Core
`modules/core/participant/domain/src/main/java/org/mojave/core/participant/domain/model/Fsp.java`

## Development Workflow

### Adding New Features
1. Identify affected bounded context
2. Define command/query in contract layer
3. Implement business logic in domain layer
4. Add REST API in admin/intercom layer
5. Publish events via producer if needed
6. Update caching in store layer
7. Write integration tests

### Debugging Flows
1. Enable DEBUG logging for specific packages
2. Trace transaction ID through all modules
3. Check Kafka topics for event flow
4. Examine Redis cache for stale data
5. Query database for state verification

## Related Documentation

- [Product Documentation](../product/README.md) - Business-focused documentation
- [Main Documentation Index](../README.md) - Overall documentation navigation
- [Architecture Assessment](../../modules/Mojave_Architecture_Assessment.md) - Detailed architecture analysis

## Contributing

When contributing to Mojave:

1. Follow hexagonal architecture pattern
2. Use CQRS for clear separation
3. Validate at construction with `Objects.requireNonNull()`
4. Use strongly-typed identifiers
5. Follow existing naming conventions
6. Add integration tests
7. Update relevant documentation
8. Include mermaid diagrams for complex flows

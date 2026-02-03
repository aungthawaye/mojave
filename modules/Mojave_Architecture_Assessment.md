# Mojave Payment Switch - Architecture Assessment Report

**Assessment Date:** January 30, 2026
**Codebase Version:** 1.0.0
**Technology Stack:** Java 25, Spring Boot 4.0.1, MySQL, Kafka, Redis
**Protocol:** FSPIOP v2.0 (Financial Services Provider Interoperability Protocol)

---

## Executive Summary

Mojave is a well-architected instant payment switch implementing the FSPIOP protocol for financial services interoperability. The codebase demonstrates strong adherence to Domain-Driven Design principles, CQRS patterns, and enterprise-grade infrastructure patterns. However, critical gaps exist in test coverage that must be addressed before production deployment.

### Overall Assessment: **B+ (Strong Architecture, Weak Testing)**

| Category | Score | Notes |
|----------|-------|-------|
| Architecture Design | A | Clean hexagonal/onion architecture with well-defined bounded contexts |
| Code Quality | A- | Consistent patterns, strong typing, proper use of Java records |
| Security Implementation | A- | JWS signing, Vault integration, proper authentication flows |
| Domain Modeling | A | Rich domain models with behavior, proper aggregates |
| Infrastructure Patterns | A | Production-ready connection pooling, caching, messaging |
| Test Coverage | F | **0 test files identified - critical gap** |
| Documentation | C+ | Minimal inline docs, basic module README |

---

## 1. High-Level Architecture

### 1.1 Module Structure

```
mojave/
├── component/          # Infrastructure cross-cutting concerns
│   ├── flyway         # Database migrations
│   ├── jpa            # Persistence with read/write splitting
│   ├── kafka          # Message broker configuration
│   ├── redis          # Caching layer (Redisson)
│   ├── vault          # HashiCorp Vault integration
│   └── web            # HTTP/REST utilities
├── scheme/            # Protocol definitions
│   └── fspiop         # FSPIOP v2.0 generated models
├── core/              # Domain bounded contexts
│   ├── participant    # FSP/Hub/Oracle management
│   ├── accounting     # Double-entry ledger system
│   ├── transaction    # Transaction lifecycle
│   ├── wallet         # Position/balance management
│   ├── settlement     # Settlement processing
│   └── common         # Shared datatypes
├── connector/         # External integrations
│   ├── adapter        # Protocol adapters
│   └── gateway        # API gateway
├── provider/          # Storage implementations
│   └── ledger/mysql   # MySQL ledger with stored procedures
├── operation/         # Administrative use cases
└── rail/              # Protocol implementations
    └── fspiop         # FSPIOP rail services
```

### 1.2 Bounded Contexts

The system properly separates concerns into distinct bounded contexts:

| Context | Responsibility | Key Aggregates |
|---------|---------------|----------------|
| **Participant** | FSP lifecycle, endpoints, currencies | Hub, Fsp, Oracle, Ssp |
| **Accounting** | Chart of accounts, flow definitions, posting | Chart, Account, FlowDefinition |
| **Transaction** | Transaction lifecycle, audit trail | Transaction, TransactionStep |
| **Wallet** | Position management, NDC limits | Balance, Position |
| **Settlement** | Settlement windows, batch processing | Settlement |

### 1.3 Layer Architecture

Each bounded context follows a clean hexagonal architecture:

```
contract/     → API contracts, commands, queries, DTOs, exceptions
domain/       → Entities, value objects, repositories, command handlers
admin/        → Administrative REST APIs
intercom/     → Internal service-to-service REST APIs
producer/     → Kafka message publishers
consumer/     → Kafka message listeners
store/        → Read-optimized caching layer
```

**Assessment:** Excellent separation of concerns. The contract layer provides a stable API boundary, enabling independent evolution of implementations.

---

## 2. Design Patterns Analysis

### 2.1 Command/Query Segregation (CQRS)

The codebase implements a clean CQRS pattern:

```java
// Command interface pattern
public interface PostLedgerFlowCommand {
    Output execute(Input input);
    record Input(TransactionType type, Currency currency, ...) { }
    record Output(TransactionId transactionId, List<Movement> movements) { }
}

// Handler implementation
@Service
public class PostLedgerFlowCommandHandler implements PostLedgerFlowCommand {
    @Override
    public Output execute(Input input) { ... }
}
```

**Strengths:**
- Type-safe input/output contracts using Java records
- Single responsibility per handler
- Easy to test in isolation
- Clear separation of reads (queries) and writes (commands)

### 2.2 Domain-Driven Design

Rich domain models encapsulate business logic:

```java
@Entity
public class Fsp extends JpaEntity<FspId> implements DataConversion<FspData> {
    // Behavior methods - not just getters/setters
    public void activate() { ... }
    public void deactivate() { ... }
    public void terminate() { ... }
    public FspCurrency addCurrency(Currency currency) { ... }
    public boolean isCurrencySupported(Currency currency) { ... }
}
```

**Strengths:**
- Entities contain behavior, not just data
- Proper aggregate boundaries (Fsp owns FspCurrency, FspEndpoint)
- Value objects for identifiers (FspId, AccountId, TransactionId)
- Immutability where appropriate

### 2.3 Repository Pattern with Specifications

```java
public interface TransactionRepository
    extends JpaRepository<Transaction, TransactionId>,
            JpaSpecificationExecutor<Transaction> {

    class Filters {
        public static Specification<Transaction> completedDuring(Instant from, Instant to) { ... }
        public static Specification<Transaction> withId(TransactionId id) { ... }
    }
}
```

**Strengths:**
- Composable query specifications
- Type-safe filtering
- Encapsulated query logic

### 2.4 Event-Driven Architecture

Kafka-based asynchronous messaging for cross-context communication:

```java
@Service
public class CloseTransactionPublisher {
    public void publish(CloseTransactionCommand.Input input) {
        kafkaTemplate.send(TopicNames.CLOSE_TRANSACTION,
            input.transactionId().getId().toString(), input);
    }
}

@KafkaListener(topics = TopicNames.CLOSE_TRANSACTION)
public void handle(CloseTransactionCommand.Input input, Acknowledgment ack) {
    closeTransactionCommand.execute(input);
    ack.acknowledge();
}
```

**Note:** The pattern uses command re-invocation rather than pure event sourcing. Commands are published to Kafka and re-executed in consumers.

---

## 3. Infrastructure Analysis

### 3.1 Database Layer

**Read/Write Splitting:**
```java
@Aspect
public class ReadAspect {
    @Around("@annotation(org.mojave.component.jpa.routing.Read)")
    public Object around(ProceedingJoinPoint pjp) {
        RoutingDataSource.Context.set(RoutingDataSource.Keys.READ);
        try { return pjp.proceed(); }
        finally { RoutingDataSource.Context.clear(); }
    }
}
```

**HikariCP Configuration:** Production-optimized with:
- Statement caching (500 statements, 4096 SQL limit)
- Batch rewriting enabled
- Server-side prepared statements
- Aggressive timeout settings (fail-fast philosophy)

**Flyway Migrations:** Versioned schema migrations with stored procedures for critical operations.

### 3.2 MySQL Stored Procedure for Ledger

The ledger posting uses a stored procedure for atomic double-entry bookkeeping:

```java
try (var stm = con.prepareCall("{call sp_post_ledger_batch_with_movements(?)}")) {
    stm.setString(1, postingJson);
    var hasResults = stm.execute();
    // Handle SUCCESS, ERROR, IGNORED results
}
```

**Assessment:** Excellent choice for financial atomicity. The stored procedure handles:
- Duplicate posting prevention
- Insufficient balance checks
- Overdraft limit enforcement
- Atomic batch posting

### 3.3 Caching Layer

Multi-tier caching with Redisson:
- **AccountCache:** By ID, code, chart entry, currency
- **FlowDefinitionCache:** By transaction type + currency
- **ParticipantStore:** FSP endpoint lookups

**Configuration:**
- Dual-client architecture (ops vs pub/sub)
- LZ4 compression codec
- Adaptive pool sizing based on CPU cores

### 3.4 Kafka Configuration

- Producer/Consumer factory patterns
- JSON serialization with Jackson
- Manual acknowledgment for at-least-once delivery
- Partitioning by entity ID for ordering guarantees

---

## 4. Security Implementation

### 4.1 FSPIOP Authentication

```java
public class FspiopServiceGatekeeper {
    public Authentication authenticate(HttpServletRequest request) {
        verifyRequestAge(request);      // Prevent replay attacks
        verifyFsps(request);            // Validate source/destination
        return authenticateUsingJws(request);  // JWS signature verification
    }
}
```

**Security Features:**
- Request age verification (configurable, default 5s)
- FSP source/destination validation
- JWS signature verification using public keys
- Per-FSP key management

### 4.2 JWS Signing

```java
public class FspiopSignature {
    public static Header sign(PrivateKey privateKey, Map<String, String> headers, String payload) {
        var token = Jwt.sign(privateKey, headers, payload);
        return new Header(token.signature(), token.header());
    }
}
```

### 4.3 Secrets Management

HashiCorp Vault integration for secrets:
- Token-based authentication
- KV v2 engine (versioned secrets)
- Environment/property configuration

---

## 5. Code Quality Assessment

### 5.1 Naming Conventions

| Element | Convention | Example | Assessment |
|---------|-----------|---------|------------|
| Packages | lowercase, domain-aligned | `org.mojave.core.accounting.domain` | ✅ Excellent |
| Classes | PascalCase, noun-based | `PostLedgerFlowCommandHandler` | ✅ Excellent |
| Methods | camelCase, verb-based | `createLedgerBalance()` | ✅ Excellent |
| Constants | UPPER_SNAKE | `SQL_INSERT_LEDGER_BALANCE` | ✅ Excellent |

### 5.2 SOLID Principles

| Principle | Adherence | Evidence |
|-----------|-----------|----------|
| **Single Responsibility** | Strong | One handler per command, clear module boundaries |
| **Open/Closed** | Good | Interface-based commands, extensible via new implementations |
| **Liskov Substitution** | Good | Proper inheritance hierarchies (JpaEntity, DataConversion) |
| **Interface Segregation** | Excellent | Fine-grained command interfaces |
| **Dependency Inversion** | Excellent | Constructor injection, interface contracts |

### 5.3 Java Modern Features

- **Java Records:** Extensively used for DTOs, settings, and value objects
- **Pattern Matching:** Switch expressions for error handling
- **Sealed Classes:** Not observed (opportunity for improvement)
- **Text Blocks:** Used for SQL statements

### 5.4 Code Smells Identified

1. **Assert Statements for Validation:**
   ```java
   public PostLedgerFlowCommandHandler(...) {
       assert accountCache != null;  // Should be Objects.requireNonNull()
   }
   ```
   Asserts can be disabled; use explicit null checks.

2. **Generic RuntimeException Wrapping:**
   ```java
   throw new RuntimeException(new DuplicatePostingException(...));
   ```
   Consider a custom exception hierarchy.

3. **Hardcoded Feature Flags:**
   ```java
   if (!ENABLED) return;  // Static flag in AddStepPublisher
   ```
   Should use configuration management.

---

## 6. Critical Findings

### 6.1 TEST COVERAGE: CRITICAL GAP

**Finding:** Zero test files identified in the codebase.

```
Test files found: 0
Main source files: 1,218
```

**Risk Assessment:** CRITICAL

**Recommendations:**
1. Implement unit tests for all command handlers
2. Integration tests for repository queries
3. Contract tests for intercom APIs
4. End-to-end tests for payment flows
5. Performance/load testing for stored procedures

### 6.2 Documentation Gaps

- Limited JavaDoc on public APIs
- No architecture decision records (ADRs)
- Missing API documentation (OpenAPI specs present but not comprehensive)

### 6.3 Error Handling Observations

- Proper exception hierarchies per domain
- Consistent error response format via `FspiopServiceControllerAdvice`
- Some silent error logging in Kafka consumers (potential message loss)

---

## 7. FSPIOP Protocol Implementation

### 7.1 Protocol Compliance

The implementation covers the core FSPIOP flows:

| Flow | Status | Notes |
|------|--------|-------|
| Party Lookup (GET/PUT /parties) | ✅ Implemented | Forward-based routing |
| Quote Request (POST /quotes) | ✅ Implemented | Async response handling |
| Transfer Prepare (POST /transfers) | ✅ Implemented | Multi-step orchestration |
| Transfer Fulfil (PUT /transfers) | ✅ Implemented | Commit/rollback handling |
| Error Handling | ✅ Implemented | Standard FSPIOP error codes |

### 7.2 Transfer Flow Architecture

```
POST /transfers → TransfersController
    → PostTransfersEvent (async)
    → PostTransfersCommandHandler
        Step 1: UnwrapRequestStep (decrypt ILP)
        Step 2: ReceiveTransferStep (create record)
        Step 3: ReservePayerPositionStep (reserve funds)
        Step 4: ReserveTransferStep (state transition)
        Step 5: ForwardToDestinationStep (to payee FSP)
    → Return 202 Accepted
```

---

## 8. Recommendations

### 8.1 Immediate Actions (P0)

1. **Implement Test Suite**
   - Target: 80% code coverage minimum
   - Priority: Command handlers, repository queries, ledger operations

2. **Add Retry Semantics to Kafka Consumers**
   - Current silent error logging risks message loss
   - Implement dead-letter queues

3. **Replace Assert with Explicit Validation**
   - Use `Objects.requireNonNull()` or custom validation

### 8.2 Short-Term Improvements (P1)

1. **Add Circuit Breakers** for external FSP calls (Resilience4j)
2. **Implement Distributed Tracing** (OpenTelemetry)
3. **Add Health Checks** for all infrastructure dependencies
4. **Generate Comprehensive OpenAPI Documentation**

### 8.3 Long-Term Enhancements (P2)

1. **Consider Event Sourcing** for complete audit trails
2. **Add GraphQL API** for flexible querying
3. **Implement API Versioning Strategy**
4. **Add Performance Benchmarking Suite**

---

## 9. Conclusion

Mojave demonstrates enterprise-grade architecture suitable for production payment processing with the following highlights:

**Strengths:**
- Clean DDD implementation with proper bounded contexts
- Production-ready infrastructure patterns
- Strong security implementation
- Well-structured module hierarchy
- Modern Java features and idioms

**Critical Gaps:**
- Complete absence of automated tests
- Limited documentation

**Recommendation:** The architecture is sound and follows industry best practices. However, **the system should not be deployed to production until comprehensive test coverage is established**. The absence of tests in a financial system represents unacceptable operational risk.

---

*Report generated by architectural analysis of Mojave codebase v1.0.0*

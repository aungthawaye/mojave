# Transaction Tracking

## Overview

Transaction tracking provides complete visibility into every payment transaction processed by Mojave. The module records every step, maintains comprehensive audit trails, and enables powerful query capabilities for monitoring, reconciliation, and compliance.

## Transaction Query Capabilities

### Query by Transaction ID

```java
Transaction getTransaction(TransactionId transactionId);
```

Returns complete transaction details including:
- Transaction metadata
- All transaction steps
- Position updates
- Accounting movements
- Settlement records

### Query by FSP

**Transactions involving an FSP:**
```java
List<Transaction> getTransactions(
    FspId fspId,
    Instant from,
    Instant to
);
```

**Payer transactions:**
```java
List<Transaction> getPayerTransactions(
    FspId payerFspId,
    Instant from,
    Instant to
);
```

**Payee transactions:**
```java
List<Transaction> getPayeeTransactions(
    FspId payeeFspId,
    Instant from,
    Instant to
);
```

### Query by Phase

```java
List<Transaction> getTransactionsByPhase(
    TransactionPhase phase
);
```

**Common Queries:**
- INITIATED: Recently created
- PREPARED: Awaiting confirmation
- COMMITTED: Successfully completed
- ABORTED: Failed or cancelled

### Query by Date Range

```java
List<Transaction> getTransactions(
    Instant from,
    Instant to,
    Integer limit,
    Integer offset
);
```

Supports pagination for large result sets.

### Query by Amount Range

```java
List<Transaction> getTransactionsByAmountRange(
    Currency currency,
    BigDecimal minAmount,
    BigDecimal maxAmount,
    Instant from,
    Instant to
);
```

Useful for identifying high-value transactions.

## Transaction Step Tracking

### Step Query

**Get all steps for transaction:**
```java
List<TransactionStep> getSteps(TransactionId transactionId);
```

**Get failed steps:**
```java
List<TransactionStep> getFailedSteps(TransactionId transactionId);
```

### Step Analysis

**Average step duration:**
```java
Duration getAverageStepDuration(String stepName, Instant from, Instant to);
```

**Step failure rate:**
```java
BigDecimal getStepFailureRate(String stepName, Instant from, Instant to);
```

## Complete Audit Trail

### Transaction Audit Record

For each transaction, the system maintains:

**Transaction Details:**
- Transaction ID, type, currency, amount
- Payer FSP, payee FSP
- Current phase
- All timestamps (initiated, prepared, committed, closed)

**Position Changes:**
- Payer position updates (old/new amounts, reserved)
- Payee position updates
- Position update IDs for traceability

**Accounting Movements:**
- All ledger entries
- Account IDs and owners
- Debit/credit amounts
- Movement IDs and timestamps

**Settlement Information:**
- Settlement record ID
- SSP used
- Settlement status and timestamps

**Step-by-Step Execution:**
- All steps executed
- Step status (success/failure)
- Error details if failed
- Duration of each step
- Sequence order

### Audit Export

Export complete audit trail:

```java
AuditReport exportAudit(
    TransactionId transactionId,
    AuditFormat format  // JSON, XML, PDF
);
```

## Transaction Statistics

### Volume Statistics

```java
record TransactionStatistics(
    long totalCount,
    long successCount,
    long failureCount,
    BigDecimal successRate,
    BigDecimal totalValue,
    BigDecimal averageValue,
    Instant earliestTransaction,
    Instant latestTransaction
) {}
```

**Query:**
```java
TransactionStatistics getStatistics(
    Instant from,
    Instant to,
    TransactionType type,
    Currency currency
);
```

### Performance Metrics

**Average transaction duration:**
```
Duration = AVG(committed_at - initiated_at)
```

**Transactions per second:**
```
TPS = COUNT(*) / (period_end - period_start).total_seconds()
```

**Phase distribution:**
```sql
SELECT current_phase, COUNT(*)
FROM transaction
WHERE transaction_at BETWEEN ? AND ?
GROUP BY current_phase;
```

## Transaction Monitoring

### Real-Time Dashboard

**Key Metrics:**
- Transactions per minute
- Success rate
- Average duration
- Failed transaction count
- Stuck transaction count

**Phase Distribution:**
- INITIATED count
- PREPARED count
- COMMITTED count
- ABORTED count
- CLOSED count

### Transaction Alerts

**Stuck Transaction:**
```java
if (phase == PREPARED && age > 30_SECONDS) {
    alert("Transaction stuck in PREPARED: " + transactionId);
}
```

**High Failure Rate:**
```java
if (failureRate > 0.05) {  // 5%
    alert("High transaction failure rate: " + failureRate);
}
```

**Long Duration:**
```java
if (duration > maxDuration) {
    alert("Slow transaction: " + transactionId +
          " - Duration: " + duration);
}
```

## Transaction Reports

### Transaction Summary Report

```
Transaction Summary Report
Period: 2026-02-04

Volume:
  Total Transactions: 12,547
  Successful: 12,483 (99.5%)
  Failed: 64 (0.5%)

Value:
  Total Value: $15,250,000
  Average: $1,215
  Minimum: $1.00
  Maximum: $100,000

Performance:
  Average Duration: 1.2 seconds
  P50 Duration: 0.8 seconds
  P95 Duration: 2.5 seconds
  P99 Duration: 4.2 seconds

By Phase:
  INITIATED: 15
  PREPARED: 23
  COMMITTED: 12,245
  ABORTED: 64
  CLOSED: 12,200
```

### FSP Transaction Report

```
FSP Transaction Report
FSP: BANK_A
Currency: USD
Period: 2026-02-04

As Payer:
  Count: 5,247
  Total Sent: $6,500,000
  Average: $1,239
  Success Rate: 99.8%

As Payee:
  Count: 4,893
  Total Received: $6,120,000
  Average: $1,251
  Success Rate: 99.6%

Net Position Change: -$380,000

Top Counterparties:
  1. MOBILE_A: 2,341 transactions, $2,850,000
  2. BANK_B: 1,789 transactions, $2,150,000
  3. FINTECH_C: 1,245 transactions, $1,500,000
```

### Failed Transaction Report

```
Failed Transactions Report
Period: 2026-02-04

Transaction   Payer    Payee     Amount    Failure Reason              Time
TXN_001234    BANK_A   MOBILE_B  $1,000    POSITION_LIMIT_EXCEEDED    10:15:23
TXN_001567    BANK_B   BANK_C    $5,000    INSUFFICIENT_BALANCE       11:23:45
TXN_002234    MOBILE_A BANK_A    $500      TRANSFER_EXPIRED           14:56:12

Summary:
Total Failed: 64
  Position Limit: 25
  Insufficient Balance: 18
  Transfer Expired: 12
  Technical Error: 9
```

## Transaction Reconciliation

### Daily Reconciliation

**Transaction Count Validation:**
```sql
SELECT COUNT(*) FROM transaction
WHERE DATE(transaction_at) = CURRENT_DATE
  AND current_phase IN ('COMMITTED', 'CLOSED');
```

**Amount Validation:**
```sql
SELECT SUM(amount) FROM transaction
WHERE DATE(transaction_at) = CURRENT_DATE
  AND current_phase = 'COMMITTED';
```

**Cross-Module Validation:**
```
Transaction committed = Position update committed
Transaction committed = Ledger movement created
Transaction committed = Settlement record created
```

### Reconciliation Reports

Compare transactions against:
- FSP records
- Accounting ledger
- Position updates
- Settlement records

Identify and resolve discrepancies.

## Administrative Operations

### Transaction Lookup Tools

**Search by criteria:**
- Transaction ID
- FSP code
- Amount range
- Date range
- Phase
- Transaction type

**Advanced filters:**
- Combine multiple criteria
- Sort by various fields
- Export results

### Transaction Investigation

**Deep dive capabilities:**
- View complete transaction history
- Trace all related records
- Analyze step execution
- Review error details
- Check retry attempts

## See Also

- [Transaction Lifecycle](../02-core-concepts/transaction-lifecycle.md) - Transaction concepts
- [Transaction Module](../../technical/02-core-modules/transaction-module.md) - Technical implementation
- [Transfer Flow](../../technical/03-flows/transfer-flow.md) - Complete transfer process

# Settlement Processing

## Overview

Settlement processing manages the actual movement of money between FSPs through Settlement Service Providers (SSPs). The module provides flexible settlement definitions with multi-tier FSP filtering, automatic settlement provider matching, and complete settlement lifecycle management.

## Settlement Definition Configuration

### Creating Settlement Definitions

Define rules for settlement provider selection:

**Settlement Definition Attributes:**
- Name (unique identifier)
- Currency
- Payer filter group
- Payee filter group
- Desired SSP
- Start date
- Activation status

**Example:**
```
Name: "Tier 1 Banks USD Settlement"
Currency: USD
Payer Filter Group: [BANK_A, BANK_B, BANK_C]
Payee Filter Group: [BANK_A, BANK_B, BANK_C]
Desired SSP: CENTRAL_BANK_SSP
Start Date: 2026-01-01
Status: ACTIVE
```

### Filter Group Configuration

**Create Filter Group:**
```java
CreateFilterGroupCommand.execute(
    settlementDefinitionId,
    filterGroupType,  // PAYER or PAYEE
    description
);
```

**Add FSPs to Filter Group:**
```java
AddFilterItemCommand.execute(
    filterGroupId,
    fspId
);
```

**Example Configuration:**
```
Settlement Definition: "Cross-Tier Settlement"
  Payer Filter Group:
    - BANK_A
    - BANK_B
  Payee Filter Group:
    - MOBILE_MONEY_A
    - MOBILE_MONEY_B
  Desired SSP: COMMERCIAL_SSP
```

### Multi-Tier Filtering

Build sophisticated settlement routing with filter hierarchies:

**Example Multi-Tier Setup:**
```
# High-value bank-to-bank (RTGS)
Definition 1: "RTGS Settlement"
  Currency: USD
  Payer: [BANK_A, BANK_B, BANK_C]
  Payee: [BANK_A, BANK_B, BANK_C]
  SSP: CENTRAL_BANK_SSP
  Type: RTGS

# Mobile money internal (CGS)
Definition 2: "Mobile Money Settlement"
  Currency: USD
  Payer: [MOBILE_A, MOBILE_B]
  Payee: [MOBILE_A, MOBILE_B]
  SSP: MOBILE_MONEY_SSP
  Type: CGS

# Cross-segment (DFN)
Definition 3: "Cross-Segment Settlement"
  Currency: USD
  Payer: [BANK_A, BANK_B]
  Payee: [MOBILE_A, MOBILE_B]
  SSP: COMMERCIAL_SSP
  Type: DFN
```

## Settlement Provider Matching

### Matching Algorithm

**Process:**
1. Extract transfer details (currency, payer FSP, payee FSP)
2. Query active settlement definitions for currency
3. Iterate in priority order
4. Match payer FSP against payer filter group
5. Match payee FSP against payee filter group
6. Return desired SSP on match
7. Use default SSP if no match

**Code Reference:**
```java
public boolean matches(Currency currency,
                      FspId payerFspId,
                      FspId payeeFspId) {
    return this.currency.equals(currency)
        && this.payerFilterGroup.fspExists(payerFspId)
        && this.payeeFilterGroup.fspExists(payeeFspId);
}
```

**Source:** `/Users/aungthawaye/Development/Jdev/mojave/modules/core/settlement/domain/src/main/java/org/mojave/core/settlement/domain/model/SettlementDefinition.java`

### Matching Example

**Transfer:** BANK_A → MOBILE_A, USD $1,000

**Evaluation:**
```
Check Definition 1 (RTGS Settlement):
  Currency: USD ✓
  Payer (BANK_A) in [BANK_A, BANK_B, BANK_C]: ✓
  Payee (MOBILE_A) in [BANK_A, BANK_B, BANK_C]: ✗
  Result: No match

Check Definition 2 (Mobile Money Settlement):
  Currency: USD ✓
  Payer (BANK_A) in [MOBILE_A, MOBILE_B]: ✗
  Result: No match

Check Definition 3 (Cross-Segment Settlement):
  Currency: USD ✓
  Payer (BANK_A) in [BANK_A, BANK_B]: ✓
  Payee (MOBILE_A) in [MOBILE_A, MOBILE_B]: ✓
  Result: MATCH!

Selected SSP: COMMERCIAL_SSP (DFN)
```

## Settlement Lifecycle Management

### Settlement Record Creation

Automatically created when transfer commits:

**Settlement Record Attributes:**
- Settlement record ID
- Transaction ID
- SSP ID
- Payer FSP ID
- Payee FSP ID
- Currency
- Amount
- Settlement type (DFN, CGS, RTGS)
- Status (INITIATED, PREPARED, COMPLETED, FAILED)
- Timestamps

**Example:**
```
Settlement Record: SETTLE_123456
Transaction: TXN_789012
SSP: COMMERCIAL_SSP
Payer FSP: BANK_A
Payee FSP: MOBILE_A
Currency: USD
Amount: $1,000.00
Type: DFN
Status: INITIATED
Initiated At: 2026-02-04T10:30:00Z
```

### Settlement Initiation

**Command:**
```java
InitiateSettlementCommand.execute(transactionId);
```

**Process:**
1. Get transaction details
2. Run settlement provider matching
3. Create settlement record
4. Set status to INITIATED
5. Timestamp initiation

### Settlement Preparation

**Command:**
```java
PrepareSettlementCommand.execute(settlementRecordId);
```

**Process:**
1. Build settlement instruction
2. Send to SSP
3. Update status to PREPARED
4. Timestamp preparation
5. Wait for SSP confirmation

**Settlement Instruction:**
```json
{
  "settlementId": "SETTLE_123456",
  "transactionId": "TXN_789012",
  "settlementType": "DFN",
  "currency": "USD",
  "amount": "1000.00",
  "payerFsp": "BANK_A",
  "payeeFsp": "MOBILE_A",
  "settlementDate": "2026-02-04T23:59:59Z"
}
```

### Settlement Completion

**Trigger:** SSP confirmation received

**Process:**
1. Receive SSP confirmation
2. Update status to COMPLETED
3. Record SSP reference
4. Timestamp completion
5. Update FSP settlement positions
6. Generate notifications

## Batch Settlement Support

### Deferred Net Settlement (DFN)

**Daily Batch Process:**
```
End of Day Settlement - 2026-02-04 23:59

Group transactions by:
  - Settlement provider
  - Currency
  - FSP

Calculate net positions:
  BANK_A USD:
    Credits: $5,250,000
    Debits: $5,180,000
    Net: +$70,000 (to receive)

  MOBILE_A USD:
    Credits: $3,890,000
    Debits: $3,920,000
    Net: -$30,000 (to pay)

Create settlement batch:
  Batch ID: BATCH_20260204
  SSP: COMMERCIAL_SSP
  Currency: USD
  Records: 15
  Total Debits: $1,250,000
  Total Credits: $1,250,000
  Status: INITIATED
```

### Continuous Gross Settlement (CGS)

**Real-Time Individual Settlement:**
```
Transaction commits → Immediate settlement

10:30:00 - TXN_001: $1,000 → SETTLE_001 (INITIATED)
10:30:01 - Send to SSP
10:30:02 - SETTLE_001 (COMPLETED)

10:35:00 - TXN_002: $500 → SETTLE_002 (INITIATED)
10:35:01 - Send to SSP
10:35:02 - SETTLE_002 (COMPLETED)
```

### Real-Time Gross Settlement (RTGS)

**Immediate Central Bank Settlement:**
```
Transfer and settlement happen atomically

Transaction phases:
  1. Reserve payer position
  2. Reserve in central bank (RTGS)
  3. Post to ledger
  4. Commit positions
  5. Confirm RTGS completion

All steps must complete or all rollback
```

## Settlement Query Capabilities

### Query Settlement Records

**By Transaction:**
```java
SettlementRecord getSettlement(TransactionId transactionId);
```

**By FSP:**
```java
List<SettlementRecord> getSettlements(
    FspId fspId,
    Instant from,
    Instant to
);
```

**By Status:**
```java
List<SettlementRecord> getSettlementsByStatus(
    SettlementStatus status
);
```

**By SSP:**
```java
List<SettlementRecord> getSettlementsByProvider(
    SspId sspId,
    Instant from,
    Instant to
);
```

## Settlement Reconciliation

### Daily Reconciliation Process

**1. Internal Reconciliation:**
```sql
SELECT COUNT(*), SUM(amount)
FROM settlement_record
WHERE DATE(initiated_at) = CURRENT_DATE
  AND status = 'COMPLETED';
```

**2. SSP Reconciliation:**
```
Hub Records vs SSP Confirmations:
  Hub: 1,247 settlements, $15,250,000
  SSP:  1,247 settlements, $15,250,000
  Match: ✓
```

**3. FSP Settlement Positions:**
```
FSP A Net Position: +$70,000
Settlement Credits: +$70,000
Match: ✓
```

### Mismatch Resolution

If reconciliation fails:
1. Identify missing settlements
2. Check for duplicates
3. Verify amounts
4. Review SSP responses
5. Compare with transaction records
6. Create adjustment entry
7. Document resolution

## Settlement Reporting

### FSP Settlement Statement

```
FSP Settlement Statement
FSP: BANK_A
Currency: USD
Period: 2026-02-04

Opening Settlement Position: $0

Settlements:
Time      Transaction   Amount      Type  SSP              Status
10:30:00  TXN_001       +$1,000     DFN   COMMERCIAL_SSP   COMPLETED
11:00:00  TXN_002       -$500       DFN   COMMERCIAL_SSP   COMPLETED
14:30:00  TXN_003       +$2,500     DFN   COMMERCIAL_SSP   COMPLETED

End of Day Net: +$3,000
Settlement Instruction: Credit $3,000 to BANK_A

Closing Settlement Position: +$3,000
```

### SSP Settlement Summary

```
SSP Settlement Summary
SSP: COMMERCIAL_SSP
Currency: USD
Period: 2026-02-04

Settlement Instructions:
  Credits (to FSPs):
    BANK_A: +$70,000
    BANK_B: +$25,000
    MOBILE_A: +$15,000
    Total Credits: $110,000

  Debits (from FSPs):
    BANK_C: -$50,000
    MOBILE_B: -$35,000
    FINTECH_A: -$25,000
    Total Debits: -$110,000

Net SSP Position: $0 (balanced)
Settlement Count: 1,247
Status: All completed
```

## Settlement Monitoring

### Real-Time Metrics

**Settlement Volume:**
- Settlements per hour
- Settlements per day
- Total settlement value

**Settlement Success Rate:**
```
Success Rate = COMPLETED / (COMPLETED + FAILED) * 100%
```

**Average Settlement Time:**
```
Avg Time = AVG(completed_at - initiated_at)
```

### Settlement Alerts

**Delayed Settlement:**
```java
if (status == PREPARED && age > maxSettlementAge) {
    alert("Settlement delayed: " + settlementId +
          " - Age: " + age + " - SSP: " + sspId);
}
```

**Settlement Failure:**
```java
if (status == FAILED) {
    alert("Settlement failed: " + settlementId +
          " - Reason: " + failureReason +
          " - Amount: " + amount);
}
```

**Reconciliation Mismatch:**
```java
if (hubTotal != sspTotal) {
    alert("Settlement reconciliation mismatch" +
          " - Hub: " + hubTotal +
          " - SSP: " + sspTotal +
          " - Difference: " + (hubTotal - sspTotal));
}
```

## Administrative Operations

### Manual Settlement

For corrections or special cases:

```java
ManualSettlementCommand.execute(
    transactionId,
    sspId,
    settlementType,
    reason,
    approver
);
```

### Settlement Retry

Retry failed settlement:

```java
RetrySettlementCommand.execute(
    settlementRecordId,
    reason
);
```

**Process:**
1. Verify settlement in FAILED status
2. Clear previous failure
3. Resend to SSP
4. Update status to PREPARED
5. Await new confirmation

### Settlement Reversal

Reverse completed settlement (rare):

```java
ReverseSettlementCommand.execute(
    settlementRecordId,
    reason,
    approver
);
```

**Requirements:**
- High-level authorization
- Documented reason
- SSP coordination
- Accounting adjustment

## See Also

- [Settlement Framework](../02-core-concepts/settlement-framework.md) - Settlement concepts
- [Participants and Roles](../02-core-concepts/participants-and-roles.md) - SSP role
- [Settlement Module](../../technical/02-core-modules/settlement-module.md) - Technical implementation
- [Settlement Flow](../../technical/03-flows/settlement-flow.md) - Technical flow details

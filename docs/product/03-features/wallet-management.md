# Wallet Management

## Overview

Wallet management provides real-time liquidity tracking through position and balance management for each FSP and currency combination. The module enforces Net Debit Cap (NDC) limits, manages the reserve-commit-rollback workflow, and ensures FSPs have sufficient funds before executing transfers.

## Position Management

### Position Creation

Positions are automatically created when an FSP is configured with a currency:

**Position Attributes:**
- Position ID (auto-generated)
- Wallet owner ID (FSP ID)
- Currency
- Amount (current position)
- Reserved (locked for in-flight transfers)
- Net Debit Cap limit
- Creation timestamp
- Last update timestamp

**Example:**
```
Position ID: POS_123
Owner: BANK001
Currency: USD
Amount: $1,000,000
Reserved: $50,000
Net Debit Cap: $500,000
Available: $1,450,000
Created: 2026-01-01T00:00:00Z
Updated: 2026-02-04T10:30:45Z
```

### Position Query

**Get Position:**
```java
Position getPosition(WalletOwnerId ownerId, Currency currency);
```

**Get All Positions for FSP:**
```java
List<Position> getPositions(WalletOwnerId ownerId);
```

**Real-Time Availability:**
```java
BigDecimal getAvailableLiquidity(WalletOwnerId ownerId, Currency currency) {
    Position position = getPosition(ownerId, currency);
    return position.getAmount()
        .subtract(position.getReserved())
        .add(position.getNetDebitCap());
}
```

## Reserve-Commit-Rollback Operations

### Reserve Position

Lock funds for a pending transfer:

**Command:**
```java
public interface ReservePositionCommand {
    Output execute(Input input);

    record Input(
        WalletOwnerId walletOwnerId,
        Currency currency,
        BigDecimal amount,
        TransactionId transactionId,
        Instant transactionAt,
        String description
    ) {}

    record Output(
        PositionUpdateId positionUpdateId,
        PositionId positionId,
        PositionAction action,         // RESERVE
        BigDecimal oldPosition,
        BigDecimal newPosition,
        BigDecimal oldReserved,
        BigDecimal newReserved,
        BigDecimal netDebitCap,
        Instant transactionAt
    ) {}
}
```

**Source:** `/Users/aungthawaye/Development/Jdev/mojave/modules/core/wallet/contract/src/main/java/org/mojave/core/wallet/contract/command/position/ReservePositionCommand.java`

**Process:**
1. Fetch current position
2. Calculate available liquidity
3. Check NDC limit
4. Increment reserved amount
5. Create position update record
6. Return position update ID

**NDC Validation:**
```java
BigDecimal effectivePosition = currentPosition - amount - currentReserved;
if (effectivePosition < -netDebitCap) {
    throw new PositionLimitExceededException(
        "Available liquidity insufficient. " +
        "Effective position would be: " + effectivePosition +
        ", NDC limit: " + -netDebitCap
    );
}
```

**Example:**
```
Before Reserve:
  Position: $1,000,000
  Reserved: $50,000
  NDC: $500,000
  Available: $1,450,000

Reserve $200,000:
  Check: $1,000,000 - $200,000 - $50,000 = $750,000 > -$500,000 ✓

After Reserve:
  Position: $1,000,000 (unchanged)
  Reserved: $250,000 (increased)
  Available: $1,250,000 (decreased)
```

### Commit Position

Finalize the transfer and release the reservation:

**Command:**
```java
CommitPositionCommand.execute(
    positionUpdateId,
    transactionId,
    transactionAt
);
```

**Process:**
1. Fetch position update by ID
2. Verify status is RESERVE
3. Decrease position by amount
4. Decrease reserved by amount
5. Update position update to COMMIT
6. Timestamp commit

**Example:**
```
Before Commit:
  Position: $1,000,000
  Reserved: $250,000

Commit $200,000:
  Position: $1,000,000 - $200,000 = $800,000
  Reserved: $250,000 - $200,000 = $50,000

After Commit:
  Position: $800,000
  Reserved: $50,000
  Net Effect: Position decreased, reservation released
```

### Rollback Position

Cancel the transfer and release the reservation:

**Command:**
```java
RollbackPositionCommand.execute(
    positionUpdateId,
    reason,
    transactionAt
);
```

**Process:**
1. Fetch position update by ID
2. Verify status is RESERVE
3. Decrease reserved by amount
4. Position unchanged
5. Update position update to ROLLBACK
6. Record reason
7. Timestamp rollback

**Example:**
```
Before Rollback:
  Position: $1,000,000
  Reserved: $250,000

Rollback $200,000:
  Reserved: $250,000 - $200,000 = $50,000
  Position: $1,000,000 (unchanged)

After Rollback:
  Position: $1,000,000 (no change)
  Reserved: $50,000
  Net Effect: Funds available again
```

## Balance Operations

### Deposit (Funding)

Add funds to FSP position:

**Command:**
```java
DepositCommand.execute(
    walletOwnerId,
    currency,
    amount,
    reference,
    description
);
```

**Effect:**
- Increase position
- Create position update (action: DEPOSIT)
- Record in accounting ledger
- Update balance

**Example:**
```
Deposit $500,000 to BANK001 USD:
  Old Position: $1,000,000
  New Position: $1,500,000
  Accounting: Debit FSP_POSITION account
```

**Use Cases:**
- Initial funding
- Additional liquidity injection
- Settlement credit

### Withdraw

Remove funds from FSP position:

**Command:**
```java
WithdrawCommand.execute(
    walletOwnerId,
    currency,
    amount,
    reference,
    description
);
```

**Validation:**
- Must have sufficient available balance
- Cannot withdraw reserved amounts
- Respects minimum balance requirements

**Example:**
```
Withdraw $200,000 from BANK001 USD:
  Position: $1,500,000
  Reserved: $100,000
  Available: $1,400,000
  Can Withdraw: $1,400,000 ✓

  Old Position: $1,500,000
  New Position: $1,300,000
```

**Use Cases:**
- Return excess liquidity
- Settlement debit
- Planned reduction

## Net Debit Cap (NDC) Management

### NDC Configuration

Set or update NDC limit for a position:

**Command:**
```java
SetNetDebitCapCommand.execute(
    positionId,
    netDebitCapLimit,
    effectiveDate
);
```

**Parameters:**
- Position ID
- NDC limit amount (positive number representing maximum negative position)
- Effective date (when limit takes effect)

**Example:**
```
Set NDC for BANK001 USD:
  Current NDC: $500,000
  New NDC: $750,000
  Effective: 2026-02-05T00:00:00Z

Effect: FSP can go up to -$750,000 position
```

### NDC Monitoring

**Check Current Usage:**
```java
BigDecimal ndcUsage = -position.getAmount().min(BigDecimal.ZERO);
BigDecimal ndcUtilization = ndcUsage.divide(ndcLimit, 4, RoundingMode.HALF_UP);
```

**Example:**
```
Position: -$300,000 (negative)
NDC Limit: $500,000
NDC Usage: $300,000
NDC Utilization: 60%
```

**Alert Thresholds:**
- 80% utilization: Warning
- 90% utilization: Critical
- 95% utilization: Emergency

### NDC Limit Adjustment

**Increase NDC:**
- Provide more credit line
- Improve liquidity flexibility
- Enable higher transaction volume

**Decrease NDC:**
- Reduce credit risk
- Require more funding
- Tighten liquidity control

**Adjustment Controls:**
- Risk assessment required
- Approval workflow
- Gradual implementation
- Impact analysis

## Multi-Currency Support

Each FSP can have positions in multiple currencies:

**Example Multi-Currency Positions:**
```
BANK001 Positions:
  USD:
    Amount: $1,000,000
    Reserved: $50,000
    NDC: $500,000
    Available: $1,450,000

  EUR:
    Amount: €750,000
    Reserved: €25,000
    NDC: €300,000
    Available: €1,025,000

  KES:
    Amount: KSh 50,000,000
    Reserved: KSh 2,000,000
    NDC: KSh 20,000,000
    Available: KSh 68,000,000
```

**Independent Management:**
- Separate NDC per currency
- Independent reservations
- Currency-specific limits
- No cross-currency interference

## Position Update Tracking

### Position Update Record

Every position change is tracked:

```java
record PositionUpdate(
    PositionUpdateId positionUpdateId,
    PositionId positionId,
    TransactionId transactionId,
    PositionAction action,           // RESERVE, COMMIT, ROLLBACK, DEPOSIT, WITHDRAW
    BigDecimal amount,
    BigDecimal oldPosition,
    BigDecimal newPosition,
    BigDecimal oldReserved,
    BigDecimal newReserved,
    String description,
    Instant reservedAt,
    Instant committedAt,
    Instant rolledBackAt,
    String rollbackReason
) {}
```

**Actions Tracked:**
- RESERVE: Funds locked
- COMMIT: Transfer finalized
- ROLLBACK: Transfer cancelled
- DEPOSIT: Funds added
- WITHDRAW: Funds removed
- ADJUST: Manual adjustment

### Query Position Updates

**By Transaction:**
```java
List<PositionUpdate> getUpdates(TransactionId transactionId);
```

**By Position:**
```java
List<PositionUpdate> getUpdates(PositionId positionId,
                                Instant from, Instant to);
```

**By Action:**
```java
List<PositionUpdate> getUpdatesByAction(PositionId positionId,
                                        PositionAction action);
```

## Concurrent Update Handling

### Optimistic Locking

Positions use versioning to handle concurrent updates:

```java
@Entity
public class Position {
    @Version
    private Long version;
    // Other fields...
}
```

**Concurrency Scenario:**
```
Thread 1: Reserve $100
Thread 2: Reserve $50

T1: Read position (version 5)
T2: Read position (version 5)
T1: Update (version 5→6) ✓ SUCCESS
T2: Update (version 5→6) ✗ VERSION CONFLICT
T2: Retry with version 6
T2: Update (version 6→7) ✓ SUCCESS
```

**Retry Strategy:**
- Automatic retry on conflict
- Exponential backoff
- Maximum 3 attempts
- Fail if still conflicting

### Lock Timeout

Position updates have timeout protection:

```java
@Lock(LockModeType.OPTIMISTIC)
@QueryHints({
    @QueryHint(name = "javax.persistence.lock.timeout", value = "5000")
})
Position findAndLockPosition(PositionId positionId);
```

## Position Reconciliation

### Daily Reconciliation

**Position vs Accounting:**
```sql
SELECT p.amount, a.balance
FROM position p
JOIN account a ON p.owner_id = a.owner_id AND p.currency = a.currency
WHERE p.amount != a.balance;
```

Should return zero rows.

**Reserved vs In-Flight:**
```sql
SELECT p.reserved,
       SUM(pu.amount) as total_reserved
FROM position p
LEFT JOIN position_update pu
    ON p.position_id = pu.position_id
   AND pu.action = 'RESERVE'
   AND pu.committed_at IS NULL
   AND pu.rolled_back_at IS NULL
GROUP BY p.position_id
HAVING p.reserved != COALESCE(SUM(pu.amount), 0);
```

**Position Movement Validation:**
```sql
SELECT p.amount - p.initial_amount as calculated_change,
       SUM(CASE
           WHEN pu.action IN ('DEPOSIT', 'COMMIT') THEN pu.amount
           WHEN pu.action IN ('WITHDRAW', 'ROLLBACK') THEN -pu.amount
           ELSE 0
       END) as actual_change
FROM position p
LEFT JOIN position_update pu ON p.position_id = pu.position_id
GROUP BY p.position_id
HAVING calculated_change != actual_change;
```

### Discrepancy Resolution

If discrepancies found:
1. Identify source of mismatch
2. Review position update audit trail
3. Compare with transaction records
4. Check for failed commits/rollbacks
5. Verify accounting postings
6. Create adjustment if needed
7. Document resolution

## Position Reporting

### Position Summary Report

```
Position Summary - BANK001
Date: 2026-02-04

Currency  Position      Reserved   NDC Limit   Available    Utilization
USD       $1,000,000    $50,000    $500,000    $1,450,000   0%
EUR       €750,000      €25,000    €300,000    €1,025,000   0%
KES       KSh 50M       KSh 2M     KSh 20M     KSh 68M      0%

Summary:
Total Currencies: 3
Total Available: Equivalent to $2,850,000 USD
```

### Position Movement Report

```
Position Movements - BANK001 USD
Period: 2026-02-04

Time      Action    Amount       Position     Reserved    Transaction
10:00:00  DEPOSIT   $1,000,000   $1,000,000   $0          -
10:30:00  RESERVE   $100,000     $1,000,000   $100,000    TXN_001
10:30:05  COMMIT    $100,000     $900,000     $0          TXN_001
11:00:00  RESERVE   $50,000      $900,000     $50,000     TXN_002
11:00:30  ROLLBACK  $50,000      $900,000     $0          TXN_002

End of Day:
Opening Position: $0
Deposits: $1,000,000
Withdrawals: $0
Net Transfers: -$100,000
Closing Position: $900,000
```

### NDC Utilization Report

```
NDC Utilization Report
Date: 2026-02-04

FSP        Currency  Position    NDC Limit   Usage      Utilization
BANK001    USD       -$100,000   $500,000    $100,000   20%
MOBILE_A   USD       -$350,000   $500,000    $350,000   70%
FINTECH_B  USD       -$475,000   $500,000    $475,000   95% ⚠️

Alert: FINTECH_B approaching NDC limit (95% utilization)
```

## Position Alerts

### Low Position Alert

```java
if (position.getAmount() < lowThreshold) {
    alert("Low position: " + ownerId + " " + currency +
          " - Position: " + position.getAmount() +
          " - Consider funding");
}
```

### High Reservation Alert

```java
BigDecimal reservationRatio = position.getReserved()
    .divide(position.getAmount().add(position.getNetDebitCap()), 4, HALF_UP);

if (reservationRatio.compareTo(BigDecimal.valueOf(0.8)) > 0) {
    alert("High reservation ratio: " + reservationRatio +
          " - May impact liquidity");
}
```

### NDC Breach Warning

```java
BigDecimal ndcUsage = position.getAmount().negate();
BigDecimal ndcUtilization = ndcUsage.divide(position.getNetDebitCap(), 4, HALF_UP);

if (ndcUtilization.compareTo(BigDecimal.valueOf(0.9)) > 0) {
    alert("Approaching NDC limit: " + ndcUtilization +
          " - Current position: " + position.getAmount() +
          " - NDC limit: " + position.getNetDebitCap());
}
```

## Administrative Operations

### Position Adjustment

For reconciliation or manual corrections:

```java
AdjustPositionCommand.execute(
    positionId,
    adjustmentAmount,
    reason,
    approver
);
```

**Controls:**
- Special authorization required
- Complete audit trail
- Documented reason
- Approval workflow
- Corresponds with accounting adjustment

### Position Freeze

Temporarily prevent position changes:

```java
FreezePositionCommand.execute(positionId, reason);
```

**Effect:**
- No new reserves allowed
- No deposits/withdrawals
- Existing reservations can commit/rollback
- Position queries still work

**Use Cases:**
- Investigation
- Dispute resolution
- Regulatory hold
- System maintenance

## See Also

- [Wallet and Positions](../02-core-concepts/wallet-and-positions.md) - Position concepts
- [Transaction Lifecycle](../02-core-concepts/transaction-lifecycle.md) - How positions change
- [Wallet Module](../../technical/02-core-modules/wallet-module.md) - Technical implementation
- [Position Management Flow](../../technical/03-flows/position-management-flow.md) - Technical flows

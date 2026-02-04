# Wallet Module

## Overview

The Wallet module manages FSP liquidity positions with Net Debit Cap (NDC) enforcement and the reserve-commit-rollback workflow for safe fund management.

## Domain Model

### ReservePositionCommand

**Source:** `/Users/aungthawaye/Development/Jdev/mojave/modules/core/wallet/contract/src/main/java/org/mojave/core/wallet/contract/command/position/ReservePositionCommand.java`

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
        PositionAction action,
        BigDecimal oldPosition,
        BigDecimal newPosition,
        BigDecimal oldReserved,
        BigDecimal newReserved,
        BigDecimal netDebitCap
    ) {}
}
```

## Position Reservation Flow

```mermaid
sequenceDiagram
    participant Caller
    participant Handler as ReservePositionCommandHandler
    participant Position
    participant DB

    Caller->>Handler: execute(Input)
    Handler->>DB: Load position (with lock)
    DB-->>Handler: Position(amount, reserved, ndcLimit)

    Handler->>Handler: Calculate: effectivePos = pos - amount - reserved
    Handler->>Handler: Check: effectivePos >= -ndcLimit

    alt NDC Check Passes
        Handler->>Position: Increment reserved
        Handler->>DB: Save position
        Handler->>DB: Create position update
        Handler-->>Caller: Output (SUCCESS)
    else NDC Exceeded
        Handler-->>Caller: PositionLimitExceededException
    end
```

### NDC Validation Logic

```java
public class PositionReservationValidator {
    public void validateNdcLimit(
        BigDecimal currentPosition,
        BigDecimal currentReserved,
        BigDecimal reservationAmount,
        BigDecimal ndcLimit
    ) {
        BigDecimal effectivePosition = currentPosition
            .subtract(reservationAmount)
            .subtract(currentReserved);

        if (effectivePosition.compareTo(ndcLimit.negate()) < 0) {
            throw new PositionLimitExceededException(
                "Effective position " + effectivePosition +
                " would exceed NDC limit " + ndcLimit
            );
        }
    }
}
```

## See Also

- [Wallet Management](../../product/03-features/wallet-management.md)
- [Wallet and Positions](../../product/02-core-concepts/wallet-and-positions.md)

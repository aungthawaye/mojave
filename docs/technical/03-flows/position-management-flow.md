# Position Management Flow

## Overview

Position management flow handles reserve, commit, and rollback operations for FSP liquidity.

## Reserve-Commit-Rollback Workflow

```mermaid
sequenceDiagram
    participant Caller
    participant Wallet
    participant Position
    participant DB

    Note over Caller,DB: RESERVE
    Caller->>Wallet: ReservePosition
    Wallet->>DB: Load position (lock)
    Wallet->>Wallet: Check NDC limit
    Wallet->>Position: Increment reserved
    Wallet->>DB: Save position
    Wallet->>DB: Create PositionUpdate (RESERVE)
    Wallet-->>Caller: positionUpdateId

    Note over Caller,DB: COMMIT (Success)
    Caller->>Wallet: CommitPosition(positionUpdateId)
    Wallet->>DB: Load position
    Wallet->>Position: Decrease amount
    Wallet->>Position: Decrease reserved
    Wallet->>DB: Update PositionUpdate (COMMIT)
    Wallet-->>Caller: Success

    Note over Caller,DB: ROLLBACK (Failure)
    Caller->>Wallet: RollbackPosition(positionUpdateId)
    Wallet->>DB: Load position
    Wallet->>Position: Decrease reserved (position unchanged)
    Wallet->>DB: Update PositionUpdate (ROLLBACK)
    Wallet-->>Caller: Success
```

## See Also

- [Wallet Management](../../product/03-features/wallet-management.md)
- [Wallet and Positions](../../product/02-core-concepts/wallet-and-positions.md)

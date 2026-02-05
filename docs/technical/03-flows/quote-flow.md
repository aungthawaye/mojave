# Quote Flow

## Overview

The quote flow enables FSPs to request fee and exchange rate information before executing a transfer.

## Quote Sequence

```mermaid
sequenceDiagram
    participant Payer FSP
    participant Hub
    participant Payee FSP

    Payer FSP->>Hub: POST /quotes
    Note over Hub: Validate request<br/>Generate ILP packet
    Hub->>Payee FSP: POST /quotes
    Payee FSP->>Payee FSP: Calculate fees<br/>Generate ILP condition
    Payee FSP-->>Hub: PUT /quotes
    Hub-->>Payer FSP: PUT /quotes
```

## See Also

- [FSPIOP v2 Implementation](../../product/04-payment-specifications/fspiop-v2-implementation.md)

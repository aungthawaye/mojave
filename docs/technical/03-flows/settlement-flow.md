# Settlement Flow

## Overview

Settlement flow manages the lifecycle of settlement from provider matching through completion.

## Settlement Lifecycle

```mermaid
stateDiagram-v2
    [*] --> Initiated: Transfer Committed
    Initiated --> Prepared: Instruction Sent to SSP
    Prepared --> Completed: SSP Confirms
    Prepared --> Failed: SSP Reports Error
    Failed --> Retry: Automatic Retry
    Retry --> Prepared
    Completed --> [*]
```

## See Also

- [Settlement Processing](../../product/03-features/settlement-processing.md)
- [Settlement Framework](../../product/02-core-concepts/settlement-framework.md)

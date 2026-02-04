# FSPIOP Transfer Processing

## Overview

PostTransfersCommandHandler orchestrates the complete transfer workflow, integrating all core modules to execute secure, atomic payments.

## PostTransfersCommandHandler

**Source:** `/Users/aungthawaye/Development/Jdev/mojave/modules/rail/fspiop/transfer/src/main/java/org/mojave/rail/fspiop/transfer/domain/command/PostTransfersCommandHandler.java`

```java
@Service
public class PostTransfersCommandHandler implements PostTransfersCommand {

    private final ParticipantStore participantStore;
    private final ReceiveTransferStep receiveTransferStep;
    private final ReserveTransferStep reserveTransferStep;
    private final ReservePayerPositionStep reservePayerPositionStep;
    private final UnwrapRequestStep unwrapRequestStep;
    private final ForwardToDestinationStep forwardToDestinationStep;

    @Override
    public Output execute(Input input) {
        // Step orchestration
        // 1. Unwrap FSPIOP request
        // 2. Create transaction
        // 3. Reserve payer position
        // 4. Post to ledger
        // 5. Reserve transfer state
        // 6. Find settlement provider
        // 7. Forward to payee FSP
    }
}
```

## Core Module Integration

### Participant Module Integration

```java
// Get FSP data from cache
FspCode payerFspCode = new FspCode(input.request().payer().fspCode());
FspData payerFsp = this.participantStore.getFspData(payerFspCode);

FspCode payeeFspCode = new FspCode(input.request().payee().fspCode());
FspData payeeFsp = this.participantStore.getFspData(payeeFspCode);
```

### Transaction Module Integration

```java
// Create transaction record
var receiveTransferOutput = this.receiveTransferStep.execute(
    new ReceiveTransferStep.Input(
        payerFsp, payeeFsp, udfTransferId, amount,
        ilpCondition, ilpPacket, agreement, expiration
    )
);

transactionId = receiveTransferOutput.transactionId();
```

### Wallet Module Integration

```java
// Reserve payer position
try {
    var reserveOutput = this.reservePayerPositionStep.execute(
        new ReservePayerPositionStep.Input(
            udfTransferId, transactionId, transactionAt,
            payerFsp, payeeFsp, currency, amount
        )
    );
    positionReservationId = reserveOutput.positionReservationId();

} catch (PositionLimitExceededException e) {
    // Abort transfer and notify
    this.abortTransferStepPublisher.publish(...);
    throw new FspiopException(FspiopErrors.PAYER_LIMIT_ERROR);
}
```

### Accounting Module Integration

Ledger posting handled by PostLedgerFlowCommand via accounting module.

### Settlement Module Integration

Settlement provider matching via FindSettlementProviderStep.

## Error Recovery

### Rollback on Position Reservation Failure

```java
catch (Exception e) {
    this.rollbackReservationStepPublisher.publish(
        new RollbackReservationStep.Input(
            udfTransferId, transactionId, transferId,
            positionReservationId, e.getMessage()
        )
    );
    this.abortTransferStepPublisher.publish(...);
    throw e;
}
```

### Error Response to Payer

```java
if (payerFsp != null) {
    final var baseUrl = payerFsp.endpoints().get(EndpointType.TRANSFERS).baseUrl();
    final var url = FspiopUrls.Transfers.putTransfersError(baseUrl, transferId);

    FspiopErrorResponder.toPayer(
        new Payer(payerFspCode.value()), exception,
        (payer, error) -> this.respondTransfers.putTransfersError(
            payer, url, error
        )
    );
}
```

## See Also

- [Transfer Flow](../03-flows/transfer-flow.md) - Complete transfer sequence
- [FSPIOP Rail Overview](fspiop-rail-overview.md) - Rail architecture
- [FSPIOP v2 Implementation](../../product/04-payment-specifications/fspiop-v2-implementation.md)

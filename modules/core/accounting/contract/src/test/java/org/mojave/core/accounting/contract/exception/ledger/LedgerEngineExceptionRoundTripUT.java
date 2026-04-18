package org.mojave.core.accounting.contract.exception.ledger;

import org.junit.jupiter.api.Test;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;

import java.util.Set;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class LedgerEngineExceptionRoundTripUT {

    @Test
    public void shouldPreserveAmountsWhenRequiredAmountNameExceptionIsRebuilt() {

        final var exception = new RequiredAmountNameNotFoundInTransactionException(
            "TRANSFER_AMOUNT", Set.of("TRANSFER_AMOUNT", "PAYEE_FSP_FEE"), new TransactionId(1L));

        final var restored = RequiredAmountNameNotFoundInTransactionException.from(
            exception.extras());

        assertEquals(exception.getRequiredAmountName(), restored.getRequiredAmountName());
        assertEquals(exception.getAmounts(), restored.getAmounts());
        assertEquals(exception.getTransactionId(), restored.getTransactionId());
    }

    @Test
    public void shouldPreserveParticipantsWhenRequiredParticipantExceptionIsRebuilt() {

        final var exception = new RequiredParticipantNotFoundInTransactionException(
            "PAYER_FSP",
            Set.of("PAYER_FSP", "PAYEE_FSP"), new TransactionId(2L));

        final var restored = RequiredParticipantNotFoundInTransactionException.from(
            exception.extras());

        assertEquals(exception.getParticipant(), restored.getParticipant());
        assertEquals(exception.getParticipants(), restored.getParticipants());
        assertEquals(exception.getTransactionId(), restored.getTransactionId());
    }

}

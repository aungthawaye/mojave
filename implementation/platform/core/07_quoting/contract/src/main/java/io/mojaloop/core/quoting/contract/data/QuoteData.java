package io.mojaloop.core.quoting.contract.data;

import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.quoting.QuotingStage;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
import io.mojaloop.core.common.datatype.identifier.quoting.UdfQuoteId;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.Objects;

public record QuoteData(QuoteId quoteId,
                        FspId payerFspId,
                        FspId payeeFspId,
                        UdfQuoteId udfQuoteId,
                        Currency currency,
                        BigDecimal amount,
                        BigDecimal fees,
                        AmountType amountType,
                        TransactionScenario scenario,
                        String subScenario,
                        TransactionInitiator initiator,
                        TransactionInitiatorType initiatorType,
                        Instant requestExpiration,
                        PartyData payer,
                        PartyData payee,
                        Instant responseExpiration,
                        BigDecimal transferAmount,
                        BigDecimal payeeFspFee,
                        BigDecimal payeeFspCommission,
                        BigDecimal payeeReceiveAmount,
                        Instant requestedAt,
                        Instant respondedAt,
                        QuotingStage stage,
                        String error,
                        List<QuoteExtensionData> extensions,
                        QuoteIlpPacketData ilpPacket) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof QuoteData quoteData)) {
            return false;
        }
        return Objects.equals(quoteId, quoteData.quoteId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(quoteId);
    }

    public record PartyData(PartyIdType partyIdType, String partyId, String subId) { }

    public record QuoteExtensionData(Direction direction, String key, String value) { }

    public record QuoteIlpPacketData(String ilpPacket, String condition) { }
}

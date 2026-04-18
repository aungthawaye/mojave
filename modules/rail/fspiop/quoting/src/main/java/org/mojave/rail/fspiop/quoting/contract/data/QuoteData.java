/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */
package org.mojave.rail.fspiop.quoting.contract.data;

import org.mojave.scheme.rule.enums.Direction;
import org.mojave.scheme.rule.enums.quoting.QuotingStage;
import org.mojave.scheme.rule.identifier.participant.FspId;
import org.mojave.scheme.rule.identifier.quoting.QuoteId;
import org.mojave.scheme.rule.identifier.quoting.UdfQuoteId;
import org.mojave.rail.fspiop.spec.AmountType;
import org.mojave.rail.fspiop.spec.Currency;
import org.mojave.rail.fspiop.spec.PartyIdType;
import org.mojave.rail.fspiop.spec.TransactionInitiator;
import org.mojave.rail.fspiop.spec.TransactionInitiatorType;
import org.mojave.rail.fspiop.spec.TransactionScenario;

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

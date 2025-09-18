/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */
package io.mojaloop.connector.sample.adapter.client;

import io.mojaloop.connector.adapter.fsp.client.FspClient;
import io.mojaloop.connector.adapter.fsp.payload.Parties;
import io.mojaloop.connector.adapter.fsp.payload.Quotes;
import io.mojaloop.connector.adapter.fsp.payload.Transfers;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Source;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.PartyComplexName;
import io.mojaloop.fspiop.spec.core.PartyPersonalInfo;

import java.math.BigDecimal;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;

public class SampleFspClient implements FspClient {

    @Override
    public Parties.Get.Response getParties(Source source, Parties.Get.Request request) throws FspiopException {

        var partyId = request.partyId();

        if (Long.parseLong(partyId) % 2 == 0) {
            throw new FspiopException(FspiopErrors.PARTY_NOT_FOUND, request.partyIdType().name() + " with Party ID (" + partyId + ") not found.");
        }

        return new Parties.Get.Response(List.of(Currency.USD, Currency.EUR, Currency.GBP, Currency.MMK), "Nezuko Kamado",
                                        new PartyPersonalInfo().complexName(new PartyComplexName().firstName("Nezuko").lastName("Kamado")));
    }

    @Override
    public void patchTransfers(Source source, Transfers.Patch.Request request) throws FspiopException {

    }

    @Override
    public Quotes.Post.Response postQuotes(Source source, Quotes.Post.Request request) throws FspiopException {

        var currency = request.originalAmount().getCurrency();
        var originalAmount = new BigDecimal(request.originalAmount().getAmount());
        var payeeFspFee = new BigDecimal("1");
        var payeeFspCommission = new BigDecimal("1");
        var payeeReceiveAmount = BigDecimal.ZERO;
        var transferAmount = new BigDecimal("0");

        if (request.amountType() == AmountType.SEND) {

            payeeReceiveAmount = originalAmount.subtract(payeeFspFee).add(payeeFspCommission);
            transferAmount = originalAmount;

        } else {

            payeeReceiveAmount = originalAmount;
            transferAmount = originalAmount.add(payeeFspFee).subtract(payeeFspCommission);

        }

        var _15minsLater = new Date(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli());
        var expiration = FspiopDates.forRequestBody(_15minsLater);

        return new Quotes.Post.Response(new Money(currency, originalAmount.toPlainString()), new Money(currency, payeeFspFee.toPlainString()),
                                        new Money(currency, payeeFspCommission.toPlainString()), new Money(currency, payeeReceiveAmount.toPlainString()),
                                        new Money(currency, transferAmount.toPlainString()), expiration);
    }

    @Override
    public Transfers.Post.Response postTransfers(Source source, Transfers.Post.Request request) throws FspiopException {

        return null;
    }

}

/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.fspiop.client.api.quotes;

import io.mojaloop.fspiop.client.api.TestSettings;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import io.mojaloop.fspiop.invoker.api.quotes.PostQuotes;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.QuotesPostRequest;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
import io.mojaloop.fspiop.spec.core.TransactionType;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.UUID;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {FspiopInvokerConfiguration.class, TestSettings.class})
public class PostQuotesUT {

    @Autowired
    PostQuotes postQuotes;

    @Test
    public void test() throws FspiopException {

        var quote = new QuotesPostRequest();

        var _15minsLater = new Date(Instant.now().plus(15, ChronoUnit.MINUTES).toEpochMilli());

        var quoteId = UUID.randomUUID().toString();

        quote.quoteId(UUID.randomUUID().toString()).transactionId(quoteId).transactionRequestId(quoteId).amountType(AmountType.SEND).amount(new Money(Currency.USD, "100"))
             .payer(new Party().partyIdInfo(new PartyIdInfo(PartyIdType.MSISDN, "987654321"))).payee(new Party().partyIdInfo(new PartyIdInfo(PartyIdType.MSISDN, "123456789")))
             .transactionType(new TransactionType().scenario(TransactionScenario.TRANSFER).initiator(TransactionInitiator.PAYER).initiatorType(TransactionInitiatorType.CONSUMER))
             .expiration(FspiopDates.forRequestBody(_15minsLater));

        this.postQuotes.postQuotes(new Payee("fsp2"), quote);
    }

}

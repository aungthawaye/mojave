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
package io.mojaloop.core.quoting.domain.command;

import io.mojaloop.core.quoting.contract.command.PostQuotesCommand;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import io.mojaloop.fspiop.spec.core.AmountType;
import io.mojaloop.fspiop.spec.core.Currency;
import io.mojaloop.fspiop.spec.core.Money;
import io.mojaloop.fspiop.spec.core.Party;
import io.mojaloop.fspiop.spec.core.PartyIdInfo;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import io.mojaloop.fspiop.spec.core.TransactionInitiator;
import io.mojaloop.fspiop.spec.core.TransactionInitiatorType;
import io.mojaloop.fspiop.spec.core.TransactionScenario;
import io.mojaloop.fspiop.spec.core.TransactionType;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.Instant;
import java.util.Map;

class PostQuotesCommandIT extends BaseDomainIT {

    @Autowired
    private PostQuotesCommand postQuotesCommand;

    @Test
    void should_create_quote_and_forward_to_payee() throws Exception {

        // Arrange
        var quoteId = java.util.UUID.randomUUID().toString();
        var expiration = Instant.now().plusSeconds(3600).toString();
        var input = buildInput(quoteId, "DFSP1", "DFSP2", Currency.USD, Currency.USD, expiration);

        // Act
        var output = this.postQuotesCommand.execute(input);

        // Assert
        Assertions.assertNotNull(output);
        var count = this.jdbc().queryForObject("select count(*) from qot_quote where udf_quote_id = ?", Integer.class, quoteId);
        Assertions.assertEquals(1, count);
    }

    @Test
    void should_send_error_when_currency_mismatch() throws Exception {

        // Arrange
        var quoteId = java.util.UUID.randomUUID().toString();
        var expiration = Instant.now().plusSeconds(3600).toString();
        var input = buildInput(quoteId, "DFSP3", "DFSP4", Currency.USD, Currency.TZS, expiration);

        // Act
        var output = this.postQuotesCommand.execute(input);

        // Assert
        Assertions.assertNotNull(output);

        var count = this.jdbc().queryForObject("select count(*) from qot_quote where udf_quote_id = ?", Integer.class, quoteId);
        Assertions.assertEquals(0, count);
    }

    private PostQuotesCommand.Input buildInput(final String quoteId,
                                               final String payerFsp,
                                               final String payeeFsp,
                                               final Currency currency,
                                               final Currency feesCurrency,
                                               final String expiration) {

        var payerInfo = new PartyIdInfo().partyIdType(PartyIdType.MSISDN).partyIdentifier("12345");
        var payeeInfo = new PartyIdInfo().partyIdType(PartyIdType.MSISDN).partyIdentifier("67890");

        var payer = new Party().partyIdInfo(payerInfo);
        var payee = new Party().partyIdInfo(payeeInfo);

        var amount = new Money().currency(currency).amount("100.00");
        var fees = new Money().currency(feesCurrency).amount("1.00");

        var txnType = new TransactionType().scenario(TransactionScenario.TRANSFER)
                                           .subScenario("BILL")
                                           .initiator(TransactionInitiator.PAYER)
                                           .initiatorType(TransactionInitiatorType.CONSUMER);

        var request = new io.mojaloop.fspiop.spec.core.QuotesPostRequest(quoteId, quoteId, payee, payer, AmountType.SEND, amount, txnType).fees(fees).expiration(expiration);

        var httpReq = new FspiopHttpRequest(new Payer(payerFsp), new Payee(payeeFsp), "POST", "/quotes", "application/json", Map.of(), Map.of(), "{}");

        return new PostQuotesCommand.Input(httpReq, request);
    }

}

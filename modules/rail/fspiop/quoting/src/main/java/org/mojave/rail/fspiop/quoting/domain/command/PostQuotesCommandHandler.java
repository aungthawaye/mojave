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

package org.mojave.rail.fspiop.quoting.domain.command;

import org.mojave.scheme.rule.enums.participant.EndpointType;
import org.mojave.scheme.rule.identifier.quoting.UdfQuoteId;
import org.mojave.scheme.rule.type.participant.FspCode;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.service.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.service.api.quotes.RespondQuotes;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.handy.FspiopDates;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopMoney;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.quoting.contract.command.PostQuotesCommand;
import org.mojave.rail.fspiop.quoting.contract.command.step.CreateQuotesRequestStep;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.async.producer.CreateQuotesRequestStepProducer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
public class PostQuotesCommandHandler implements PostQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final CreateQuotesRequestStepProducer createQuotesRequestStepProducer;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PostQuotesCommandHandler(ParticipantStore participantStore,
                                    RespondQuotes respondQuotes,
                                    ForwardRequest forwardRequest,
                                    CreateQuotesRequestStepProducer createQuotesRequestStepProducer,
                                    QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondQuotes);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(createQuotesRequestStepProducer);
        Objects.requireNonNull(quoteSettings);

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.createQuotesRequestStepProducer = createQuotesRequestStepProducer;
        this.quoteSettings = quoteSettings;
    }

    @Override
    public Output execute(Input input) {

        var udfQuoteId = new UdfQuoteId(input.quotesPostRequest().getQuoteId());

        LOGGER.info("PostQuotesCommandHandler : input: ({})", ObjectLogger.log(input));

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);

            var postQuotesRequest = input.quotesPostRequest();

            var payerFspInRequest = postQuotesRequest.getPayer().getPartyIdInfo().getFspId();
            var payeeFspInRequest = postQuotesRequest.getPayee().getPartyIdInfo().getFspId();

            if (!payeeFspInRequest.equals(payeeFspCode.value()) ||
                    !payerFspInRequest.equals(payerFspCode.value())) {

                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR,
                    "FSPs information in the request body and request header must be the same.");
            }

            var amount = postQuotesRequest.getAmount();

            FspiopMoney.validate(amount);

            var fees = postQuotesRequest.getFees();

            FspiopMoney.validate(fees);

            var currency = amount.getCurrency();

            final var payer = postQuotesRequest.getPayer().getPartyIdInfo();
            final var payee = postQuotesRequest.getPayee().getPartyIdInfo();
            final var transactionType = postQuotesRequest.getTransactionType();
            final var expiration = postQuotesRequest.getExpiration();

            Instant requestExpiration = null;

            if (expiration != null) {

                requestExpiration = FspiopDates.fromRequestBody(expiration);

                if (requestExpiration.isBefore(Instant.now())) {

                    throw new FspiopException(
                        FspiopErrors.GENERIC_VALIDATION_ERROR,
                        "The quote request from Payer FSP has expired. The expiration is : " +
                            expiration);
                }

            }

            if (this.quoteSettings.stateful()) {

                this.createQuotesRequestStepProducer.publish(new CreateQuotesRequestStep.Input(
                    payerFsp.fspId(), payeeFsp.fspId(), udfQuoteId, currency,
                    new BigDecimal(amount.getAmount()),
                    fees != null ? new BigDecimal(fees.getAmount()) : null,
                    postQuotesRequest.getAmountType(), transactionType.getScenario(),
                    transactionType.getSubScenario(), transactionType.getInitiator(),
                    transactionType.getInitiatorType(), requestExpiration, payer.getPartyIdType(),
                    payer.getPartyIdentifier(), payer.getPartySubIdOrType(), payee.getPartyIdType(),
                    payee.getPartyIdentifier(), payee.getPartySubIdOrType(),
                    postQuotesRequest.getExtensionList()));
            }

            var payeeBaseUrl = payeeFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("Forwarding request to payee FSP (Url): ({})", payeeBaseUrl);

            this.forwardRequest.forward(payeeBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payee FSP (Url): ({})", payeeBaseUrl);

        } catch (Exception e) {

            LOGGER.error("Error:", e);

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                final var url = FspiopUrls.Quotes.putQuotesError(baseUrl, udfQuoteId.getId());

                try {

                    FspiopErrorResponder.toPayer(
                        new Payer(payerFspCode.value()), e,
                        (payer, error) -> this.respondQuotes.putQuotesError(
                            sendBackTo, url,
                            error));

                } catch (Exception e1) {
                    LOGGER.error("Error:", e1);
                }
            }

        }

        LOGGER.info("PostQuotesCommandHandler : done");

        return new Output();
    }

}

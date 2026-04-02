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

import org.mojave.common.datatype.enums.participant.EndpointType;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.bootstrap.api.quotes.RespondQuotes;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.handy.FspiopDates;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.quoting.contract.command.PutQuotesCommand;
import org.mojave.rail.fspiop.quoting.contract.command.step.FindQuotesStep;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesErrorStep;
import org.mojave.rail.fspiop.quoting.contract.command.step.UpdateQuotesResponseStep;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.kafka.publisher.UpdateQuotesErrorStepPublisher;
import org.mojave.rail.fspiop.quoting.domain.kafka.publisher.UpdateQuotesResponseStepPublisher;
import org.mojave.rail.fspiop.quoting.domain.model.Quote;
import org.mojave.scheme.fspiop.core.QuotesIDPutResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
public class PutQuotesCommandHandler implements PutQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final FindQuotesStep findQuotesStep;

    private final UpdateQuotesResponseStepPublisher updateQuotesResponseStepPublisher;

    private final UpdateQuotesErrorStepPublisher updateQuotesErrorStepPublisher;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PutQuotesCommandHandler(ParticipantStore participantStore,
                                   RespondQuotes respondQuotes,
                                   ForwardRequest forwardRequest,
                                   FindQuotesStep findQuotesStep,
                                   UpdateQuotesResponseStepPublisher updateQuotesResponseStepPublisher,
                                   UpdateQuotesErrorStepPublisher updateQuotesErrorStepPublisher,
                                   QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondQuotes);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(findQuotesStep);
        Objects.requireNonNull(updateQuotesResponseStepPublisher);
        Objects.requireNonNull(updateQuotesErrorStepPublisher);
        Objects.requireNonNull(quoteSettings);

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.findQuotesStep = findQuotesStep;
        this.updateQuotesResponseStepPublisher = updateQuotesResponseStepPublisher;
        this.updateQuotesErrorStepPublisher = updateQuotesErrorStepPublisher;
        this.quoteSettings = quoteSettings;
    }

    private static boolean isCurrenciesMatch(Quote quote, QuotesIDPutResponse quoteIdPutResponse) {

        var quotedCurrency = quote.getCurrency();
        var transferCurrency = quoteIdPutResponse.getTransferAmount().getCurrency();
        var currenciesMatch = quotedCurrency.equals(transferCurrency);

        currenciesMatch = currenciesMatch && quotedCurrency.equals(
            quoteIdPutResponse.getPayeeFspFee().getCurrency());
        currenciesMatch = currenciesMatch && quotedCurrency.equals(
            quoteIdPutResponse.getPayeeFspCommission().getCurrency());
        currenciesMatch = currenciesMatch && quotedCurrency.equals(
            quoteIdPutResponse.getPayeeReceiveAmount().getCurrency());

        return currenciesMatch;
    }

    @Override
    public Output execute(Input input) {

        LOGGER.info("PutQuotesCommandHandler : input: ({})", ObjectLogger.log(input));

        var udfQuoteId = input.udfQuoteId();

        FspCode payerFspCode = null;
        FspData payerFsp = null;

        try {

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);

            var quoteIdPutResponse = input.quotesIDPutResponse();

            if (this.quoteSettings.stateful()) {

                var optQuote = this.findQuotesStep
                                   .execute(new FindQuotesStep.Input(udfQuoteId))
                                   .quote();

                if (optQuote.isEmpty()) {

                    LOGGER.warn(
                        "Receiving non-existence Quote. Just ignore it. udfQuoteId : ({})",
                        udfQuoteId.getId());

                    return new Output();
                }

                var quote = optQuote.get();
                var expiration = quoteIdPutResponse.getExpiration();
                Instant responseExpiration = null;

                if (expiration != null) {

                    try {

                        responseExpiration = FspiopDates.fromRequestBody(
                            quoteIdPutResponse.getExpiration());

                    } catch (FspiopException e) {

                        LOGGER.error("Error:", e);

                        var error =
                            "Payee FSP responded with the wrong expiration format. Responded expiration format : " +
                                quoteIdPutResponse.getExpiration();

                        this.updateQuotesErrorStepPublisher.publish(
                            new UpdateQuotesErrorStep.Input(udfQuoteId, error, null));

                        throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, error);
                    }
                }

                var currenciesMatch = isCurrenciesMatch(quote, quoteIdPutResponse);

                if (!currenciesMatch) {

                    LOGGER.error(
                        "The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.");

                    var error = "Payee FSP responded with incorrect currency information. The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.";

                    this.updateQuotesErrorStepPublisher.publish(
                        new UpdateQuotesErrorStep.Input(udfQuoteId, error, null));

                    throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, error);
                }

                var transferAmount = new BigDecimal(
                    quoteIdPutResponse.getTransferAmount().getAmount());
                var payeeFspFee = new BigDecimal(quoteIdPutResponse.getPayeeFspFee().getAmount());
                var payeeFspCommission = new BigDecimal(
                    quoteIdPutResponse.getPayeeFspCommission().getAmount());
                var payeeReceiveAmount = new BigDecimal(
                    quoteIdPutResponse.getPayeeReceiveAmount().getAmount());

                this.updateQuotesResponseStepPublisher.publish(new UpdateQuotesResponseStep.Input(
                    udfQuoteId, responseExpiration, transferAmount, payeeFspFee, payeeFspCommission,
                    payeeReceiveAmount, quoteIdPutResponse.getIlpPacket(),
                    quoteIdPutResponse.getCondition(), quoteIdPutResponse.getExtensionList()));
            }

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("Forwarding request to payer FSP (Url): ({})", payerBaseUrl);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("Done forwarding request to payer FSP (Url): ({})", payerBaseUrl);

        } catch (FspiopCommunicationException e) {

            LOGGER.error("Error:", e);

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

        LOGGER.info("PutQuotesCommandHandler : done");

        return new Output();
    }

}

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

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.jpa.transaction.TransactionContext;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.common.datatype.enums.Direction;
import org.mojave.core.common.datatype.enums.participant.EndpointType;
import org.mojave.core.common.datatype.identifier.quoting.UdfQuoteId;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.quoting.contract.command.PostQuotesCommand;
import org.mojave.rail.fspiop.quoting.contract.exception.ExpirationNotInFutureException;
import org.mojave.rail.fspiop.quoting.domain.QuotingDomainConfiguration;
import org.mojave.rail.fspiop.quoting.domain.model.Party;
import org.mojave.rail.fspiop.quoting.domain.model.Quote;
import org.mojave.rail.fspiop.quoting.domain.repository.QuoteRepository;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.component.handy.FspiopDates;
import org.mojave.rail.fspiop.component.handy.FspiopErrorResponder;
import org.mojave.rail.fspiop.component.handy.FspiopMoney;
import org.mojave.rail.fspiop.component.handy.FspiopUrls;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.bootstrap.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.Objects;

@Service
public class PostQuotesCommandHandler implements PostQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PostQuotesCommandHandler(ParticipantStore participantStore,
                                    RespondQuotes respondQuotes,
                                    ForwardRequest forwardRequest,
                                    QuoteRepository quoteRepository,
                                    PlatformTransactionManager transactionManager,
                                    QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondQuotes);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(quoteRepository);
        Objects.requireNonNull(transactionManager);
        Objects.requireNonNull(quoteSettings);

        this.participantStore = participantStore;
        this.respondQuotes = respondQuotes;
        this.forwardRequest = forwardRequest;
        this.quoteRepository = quoteRepository;
        this.transactionManager = transactionManager;
        this.quoteSettings = quoteSettings;
    }

    @Write
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

            if (fees != null && fees.getCurrency() != currency) {

                throw new FspiopException(
                    FspiopErrors.GENERIC_VALIDATION_ERROR,
                    "The currency of amount and fees must be the same.");
            }

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

                try {

                    var quote = new Quote(
                        payerFsp.fspId(), payeeFsp.fspId(), udfQuoteId, currency,
                        new BigDecimal(amount.getAmount()),
                        fees != null ? new BigDecimal(fees.getAmount()) : null,
                        postQuotesRequest.getAmountType(), transactionType.getScenario(),
                        transactionType.getSubScenario(), transactionType.getInitiator(),
                        transactionType.getInitiatorType(), requestExpiration,
                        new Party(
                            payer.getPartyIdType(), payer.getPartyIdentifier(),
                            payer.getPartySubIdOrType()),
                        new Party(
                            payee.getPartyIdType(), payee.getPartyIdentifier(),
                            payee.getPartySubIdOrType()));

                    if (postQuotesRequest.getExtensionList() != null &&
                            postQuotesRequest.getExtensionList().getExtension() != null) {

                        postQuotesRequest.getExtensionList().getExtension().forEach(extension -> {

                            quote.addExtension(
                                Direction.FROM_PAYEE, extension.getKey(), extension.getValue());
                        });

                    }

                    TransactionContext.startNew(this.transactionManager, quote.getId().toString());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                } catch (ExpirationNotInFutureException ignored) {

                } catch (Exception e) {
                    TransactionContext.rollback();
                    LOGGER.error("Error:", e);
                }

            } else {

//                LOGGER.warn(
//                    "Quoting is not stateful. Ignoring saving the quote. udfQuoteId : ({})",
//                    udfQuoteId.getId());
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

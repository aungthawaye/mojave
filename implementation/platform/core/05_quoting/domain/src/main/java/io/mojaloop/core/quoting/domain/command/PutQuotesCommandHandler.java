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

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.jpa.transaction.TransactionContext;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.participant.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.store.ParticipantStore;
import io.mojaloop.core.quoting.contract.command.PutQuotesCommand;
import io.mojaloop.core.quoting.contract.exception.ExpirationNotInFutureException;
import io.mojaloop.core.quoting.contract.exception.ReceivingAmountMismatchException;
import io.mojaloop.core.quoting.contract.exception.TransferAmountMismatchException;
import io.mojaloop.core.quoting.domain.QuotingDomainConfiguration;
import io.mojaloop.core.quoting.domain.repository.QuoteRepository;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopCommunicationException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.handy.PayeeOrServerExceptionResponder;
import io.mojaloop.fspiop.service.api.forwarder.ForwardRequest;
import io.mojaloop.fspiop.service.api.quotes.RespondQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;

import java.math.BigDecimal;
import java.time.Instant;

@Service
public class PutQuotesCommandHandler implements PutQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PutQuotesCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondQuotes respondQuotes;

    private final ForwardRequest forwardRequest;

    private final QuoteRepository quoteRepository;

    private final PlatformTransactionManager transactionManager;

    private final QuotingDomainConfiguration.QuoteSettings quoteSettings;

    public PutQuotesCommandHandler(ParticipantStore participantStore,
                                   RespondQuotes respondQuotes,
                                   ForwardRequest forwardRequest,
                                   QuoteRepository quoteRepository,
                                   PlatformTransactionManager transactionManager,
                                   QuotingDomainConfiguration.QuoteSettings quoteSettings) {

        assert participantStore != null;
        assert respondQuotes != null;
        assert forwardRequest != null;
        assert quoteRepository != null;
        assert transactionManager != null;
        assert quoteSettings != null;

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

        var udfQuoteId = input.udfQuoteId();

        LOGGER.info("({}) Executing PutQuotesCommandHandler with input: [{}]", udfQuoteId.getId(), input);

        FspCode payerFspCode = null;
        FspData payerFsp = null;
        FspCode payeeFspCode = null;
        FspData payeeFsp = null;

        try {

            payeeFspCode = new FspCode(input.request().payee().fspCode());
            payeeFsp = this.participantStore.getFspData(payeeFspCode);
            LOGGER.debug("({}) Found payee FSP: [{}]", udfQuoteId.getId(), payeeFsp);

            payerFspCode = new FspCode(input.request().payer().fspCode());
            payerFsp = this.participantStore.getFspData(payerFspCode);
            LOGGER.debug("({}) Found payer FSP: [{}]", udfQuoteId.getId(), payerFsp);

            var quoteIdPutResponse = input.quotesIDPutResponse();
            LOGGER.debug("({}) quotesIDPutResponse: [{}]", udfQuoteId.getId(), quoteIdPutResponse);

            if (this.quoteSettings.stateful()) {

                TransactionContext.startNew(this.transactionManager, udfQuoteId.getId());
                var optQuote = this.quoteRepository.findOne(QuoteRepository.Filters.withUdfQuoteId(udfQuoteId));

                if (optQuote.isEmpty()) {

                    LOGGER.warn("({}) Receiving non-existence Quote. Just ignore it.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesCommandHandler (non-existence Quote).", udfQuoteId.getId());

                    return new Output();
                }

                var quote = optQuote.get();
                var expiration = quoteIdPutResponse.getExpiration();
                Instant responseExpiration = null;

                if (expiration != null) {

                    try {

                        responseExpiration = FspiopDates.fromRequestBody(quoteIdPutResponse.getExpiration());

                    } catch (FspiopException e) {

                        LOGGER.error("({}) Wrong expiration format.", udfQuoteId.getId());
                        LOGGER.info("({}) Returning from PutQuotesCommandHandler (wrong expiration).", udfQuoteId.getId());

                        quote.error("Wrong expiration format. Response expiration format : " + quoteIdPutResponse.getExpiration());
                        this.quoteRepository.save(quote);
                        TransactionContext.commit();

                        throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, e.toErrorObject().getErrorInformation().getErrorDescription());
                    }
                }

                var quotedCurrency = quote.getCurrency();
                var transferCurrency = quoteIdPutResponse.getTransferAmount().getCurrency();

                if (!(quotedCurrency == transferCurrency && transferCurrency == quoteIdPutResponse.getPayeeFspFee().getCurrency() &&
                          transferCurrency == quoteIdPutResponse.getPayeeFspCommission().getCurrency() &&
                          transferCurrency == quoteIdPutResponse.getPayeeReceiveAmount().getCurrency())) {

                    LOGGER.error("({}) The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.", udfQuoteId.getId());
                    LOGGER.info("({}) Returning from PutQuotesCommandHandler (currency mismatch).", udfQuoteId.getId());

                    quote.error("The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.");
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR,
                                              "The currency of quote, transferAmount, payeeFspFee, payeeFspCommission and payeeReceiveAmount must be the same.");
                }

                var transferAmount = new BigDecimal(quoteIdPutResponse.getTransferAmount().getAmount());
                var payeeFspFee = new BigDecimal(quoteIdPutResponse.getPayeeFspFee().getAmount());
                var payeeFspCommission = new BigDecimal(quoteIdPutResponse.getPayeeFspCommission().getAmount());
                var payeeReceiveAmount = new BigDecimal(quoteIdPutResponse.getPayeeReceiveAmount().getAmount());

                try {

                    quote.responded(responseExpiration,
                                    transferAmount,
                                    payeeFspFee,
                                    payeeFspCommission,
                                    payeeReceiveAmount,
                                    quoteIdPutResponse.getIlpPacket(),
                                    quoteIdPutResponse.getCondition());

                    if (quoteIdPutResponse.getExtensionList() != null && quoteIdPutResponse.getExtensionList().getExtension() != null) {

                        var extensions = quoteIdPutResponse.getExtensionList().getExtension();

                        extensions.forEach(extension -> {
                            LOGGER.debug("({}) Extension found: {}", udfQuoteId.getId(), extension);
                            quote.addExtension(Direction.INBOUND, extension.getKey(), extension.getValue());
                        });
                    }

                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                } catch (TransferAmountMismatchException e) {

                    LOGGER.error("({}) Transfer amount mismatch.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, e.getMessage());

                } catch (ReceivingAmountMismatchException e) {

                    LOGGER.error("({}) Receiving amount mismatch.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, e.getMessage());

                } catch (ExpirationNotInFutureException e) {

                    LOGGER.error("({}) Quote expiration.", udfQuoteId.getId(), e);
                    quote.error(e.getMessage());
                    this.quoteRepository.save(quote);
                    TransactionContext.commit();

                    throw new FspiopException(FspiopErrors.GENERIC_PAYEE_ERROR, e.getMessage());
                }
            }

            var payerBaseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
            LOGGER.info("({}) Forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

            this.forwardRequest.forward(payerBaseUrl, input.request());
            LOGGER.info("({}) Done forwarding request to payer FSP (Url): [{}]", udfQuoteId.getId(), payerFsp);

        } catch (FspiopCommunicationException e) {

            LOGGER.error("({}) (FspiopCommunicationException) Exception occurred while executing PutQuotesCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

        } catch (Exception e) {

            LOGGER.error("({}) (Exception) Exception occurred while executing PutQuotesCommandHandler: [{}]", udfQuoteId.getId(), e.getMessage());

            if (payerFsp != null) {

                final var sendBackTo = new Payer(payerFspCode.value());
                final var baseUrl = payerFsp.endpoints().get(EndpointType.QUOTES).baseUrl();
                final var url = FspiopUrls.newUrl(baseUrl, input.request().uri() + "/error");

                try {

                    PayeeOrServerExceptionResponder.respond(new Payer(payerFspCode.value()), e, (payer, error) -> this.respondQuotes.putQuotesError(sendBackTo, url, error));

                } catch (Throwable ignored) {
                    LOGGER.error("Something went wrong while sending error response to payer FSP: ", e);
                }
            }
        }

        LOGGER.info("Returning from PutQuotesCommandHandler successfully.");
        return new Output();
    }

}

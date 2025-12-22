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

package org.mojave.connector.gateway.outbound.command;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.component.misc.pubsub.PubSubClient;
import org.mojave.connector.gateway.component.PubSubKeys;
import org.mojave.connector.gateway.data.QuotesErrorResult;
import org.mojave.connector.gateway.data.QuotesResult;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.connector.gateway.outbound.component.FspiopResultListener;
import org.mojave.fspiop.component.error.ErrorDefinition;
import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.handy.FspiopDates;
import org.mojave.fspiop.invoker.api.quotes.PostQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
class RequestQuotesCommandHandler implements RequestQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestQuotesCommandHandler.class.getName());

    private final PostQuotes postQuotes;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestQuotesCommandHandler(PostQuotes postQuotes,
                                       PubSubClient pubSubClient,
                                       ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != postQuotes;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.postQuotes = postQuotes;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        assert input != null;
        assert input.request() != null;

        final var quoteId = input.request().getQuoteId();
        MDC.put("REQ_ID", quoteId);

        final var startAt = System.nanoTime();
        var endAt = 0L;

        LOGGER.info("RequestQuotesCommandHandler : input : ({})", ObjectLogger.log(input));

        try {

            final var amount = input.request().getAmount();
            final var expiration = input.request().getExpiration();
            final var fees = input.request().getFees();

            if (expiration != null && !expiration.isBlank()) {

                final var expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {
                    throw new FspiopException(
                        FspiopErrors.GENERIC_VALIDATION_ERROR,
                        "Expiration date/time must be in the future.");
                }
            }

            if (fees != null) {

                if (fees.getCurrency() != amount.getCurrency()) {

                    throw new FspiopException(
                        FspiopErrors.GENERIC_VALIDATION_ERROR,
                        "Fees and Amount currency must have the same currency.");
                }
            }

            final var resultTopic = PubSubKeys.forQuotes(quoteId);
            final var errorTopic = PubSubKeys.forQuotesError(quoteId);

            // Listening to the pub/sub
            final var resultListener = new FspiopResultListener<>(
                this.pubSubClient,
                this.outboundSettings, QuotesResult.class, QuotesErrorResult.class);
            resultListener.init(resultTopic, errorTopic);

            this.postQuotes.postQuotes(input.payee(), input.request());

            resultListener.await();

            if (resultListener.getResponse() != null) {
                final var output = new Output(resultListener.getResponse());
                LOGGER.info(
                    "RequestQuotesCommandHandler : output : ({})", ObjectLogger.log(output));
                return output;
            }

            final var error = resultListener.getError();
            final var errorDefinition = FspiopErrors.find(
                error.errorInformation().getErrorInformation().getErrorCode());

            LOGGER.error("RequestQuotesCommandHandler : error : ({})", ObjectLogger.log(error));

            throw new FspiopException(
                new ErrorDefinition(
                    errorDefinition.errorType(),
                    error.errorInformation().getErrorInformation().getErrorDescription()));

        } finally {

            endAt = System.nanoTime();
            LOGGER.info(
                "RequestQuotesCommandHandler : done : took {} ms",
                (endAt - startAt) / 1_000_000);

            MDC.remove("REQ_ID");
        }

    }

}

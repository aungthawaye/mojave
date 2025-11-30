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

package io.mojaloop.connector.gateway.outbound.command;

import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.component.PubSubKeys;
import io.mojaloop.connector.gateway.data.QuotesErrorResult;
import io.mojaloop.connector.gateway.data.QuotesResult;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.gateway.outbound.component.FspiopResultListener;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.invoker.api.quotes.PostQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        LOGGER.info("Invoking PostQuotes : {}", input);

        assert input != null;
        assert input.request() != null;

        var quoteId = input.request().getQuoteId();
        var amount = input.request().getAmount();
        var expiration = input.request().getExpiration();
        var fees = input.request().getFees();

        if (expiration != null && !expiration.isBlank()) {

            var expireAt = FspiopDates.fromRequestBody(expiration);

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

        var resultTopic = PubSubKeys.forQuotes(quoteId);
        var errorTopic = PubSubKeys.forQuotesError(quoteId);

        // Listening to the pub/sub
        var resultListener = new FspiopResultListener<>(
            this.pubSubClient, this.outboundSettings, QuotesResult.class, QuotesErrorResult.class);
        resultListener.init(resultTopic, errorTopic);

        this.postQuotes.postQuotes(input.payee(), input.request());

        resultListener.await();

        if (resultListener.getResponse() != null) {
            return new Output(resultListener.getResponse());
        }

        var error = resultListener.getError();
        var errorDefinition = FspiopErrors.find(
            error.errorInformation().getErrorInformation().getErrorCode());

        throw new FspiopException(new ErrorDefinition(
            errorDefinition.errorType(),
            error.errorInformation().getErrorInformation().getErrorDescription()));

    }

}

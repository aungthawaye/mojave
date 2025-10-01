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
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopDates;
import io.mojaloop.fspiop.invoker.api.quotes.PostQuotes;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.text.ParseException;
import java.time.Instant;
import java.util.concurrent.CountDownLatch;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicReference;

@Service
class RequestQuotesCommandHandler implements RequestQuotesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestQuotesCommandHandler.class.getName());

    private final PostQuotes postQuotes;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestQuotesCommandHandler(PostQuotes postQuotes, PubSubClient pubSubClient, ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

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

            try {

                var expireAt = FspiopDates.fromRequestBody(expiration);

                if (expireAt.isBefore(Instant.now())) {
                    throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Expiration date/time must be in the future.");
                }

            } catch (ParseException e) {
                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "The date/time format of expiration is not valid.");
            }
        }

        if (fees != null) {

            if (fees.getCurrency() != amount.getCurrency()) {

                throw new FspiopException(FspiopErrors.GENERIC_VALIDATION_ERROR, "Fees and Amount currency must have the same currency.");
            }
        }

        var resultTopic = PubSubKeys.forQuotes(quoteId);
        var errorTopic = PubSubKeys.forQuotes(quoteId);

        // Listening to the pub/sub
        var blocker = new CountDownLatch(1);

        AtomicReference<QuotesResult> responseRef = new AtomicReference<>();
        AtomicReference<QuotesErrorResult> errorRef = new AtomicReference<>();

        var resultSubscription = this.pubSubClient.subscribe(resultTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof QuotesResult result) {
                    responseRef.set(result);
                }

                blocker.countDown();
            }

            @Override
            public Class<QuotesResult> messageType() {

                return QuotesResult.class;
            }
        }, this.outboundSettings.pubSubTimeout());

        var errorSubscription = this.pubSubClient.subscribe(errorTopic, new PubSubClient.MessageHandler() {

            @Override
            public void handle(String channel, Object message) {

                if (message instanceof QuotesErrorResult response) {
                    errorRef.set(response);
                }

                blocker.countDown();
            }

            @Override
            public Class<?> messageType() {

                return ErrorInformationObject.class;
            }
        }, 60_000);

        try {

            this.postQuotes.postQuotes(input.payee(), input.request());

            var ok = blocker.await(this.outboundSettings.putResultTimeout(), TimeUnit.MILLISECONDS);

            if (!ok) {
                throw new FspiopException(FspiopErrors.SERVER_TIMED_OUT, "Timed out while waiting for response from the Hub.");
            }

        } catch (InterruptedException ignored) {
            // Do nothing.
        } finally {
            this.pubSubClient.unsubscribe(resultSubscription);
            this.pubSubClient.unsubscribe(errorSubscription);
        }

        if (responseRef.get() != null) {
            return new Output(responseRef.get());
        }

        var error = errorRef.get();
        var errorDefinition = FspiopErrors.find(error.errorInformation().getErrorInformation().getErrorCode());

        throw new FspiopException(new ErrorDefinition(errorDefinition.errorType(), error.errorInformation().getErrorInformation().getErrorDescription()));

    }

}

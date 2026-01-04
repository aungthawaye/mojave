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
import org.mojave.connector.gateway.data.PartiesErrorResult;
import org.mojave.connector.gateway.data.PartiesResult;
import org.mojave.connector.gateway.outbound.ConnectorOutboundConfiguration;
import org.mojave.connector.gateway.outbound.component.FspiopResultListener;
import org.mojave.rail.fspiop.component.error.ErrorDefinition;
import org.mojave.rail.fspiop.component.error.FspiopErrors;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.type.Payee;
import org.mojave.rail.fspiop.invoker.api.parties.GetParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
class RequestPartiesCommandHandler implements RequestPartiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RequestPartiesCommandHandler.class.getName());

    private final GetParties getParties;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestPartiesCommandHandler(GetParties getParties,
                                        PubSubClient pubSubClient,
                                        ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != getParties;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.getParties = getParties;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        final var reqId =
            input.payee().fspCode() + "-" + input.partyIdType() + "-" + input.partyId();

        MDC.put("REQ_ID", reqId);

        var startAt = System.nanoTime();

        try {

            LOGGER.info("RequestPartiesCommand : input : ({})", ObjectLogger.log(input));

            var withSubId = input.subId() != null && !input.subId().isBlank();

            var resultTopic = PubSubKeys.forParties(
                input.payee(), input.partyIdType(), input.partyId(), input.subId());

            var errorTopic = PubSubKeys.forPartiesError(
                input.payee(), input.partyIdType(), input.partyId(), input.subId());

            var hubErrorTopic = PubSubKeys.forPartiesError(
                new Payee("hub"), input.partyIdType(),
                input.partyId(), input.subId());

            // Listening to the pub/sub
            var resultListener = new FspiopResultListener<>(
                this.pubSubClient,
                this.outboundSettings, PartiesResult.class, PartiesErrorResult.class);

            resultListener.init(resultTopic, errorTopic, hubErrorTopic);

            if (withSubId) {
                this.getParties.getParties(
                    input.payee(), input.partyIdType(), input.partyId(), input.subId());
            } else {
                this.getParties.getParties(input.payee(), input.partyIdType(), input.partyId());
            }

            resultListener.await();

            if (resultListener.getResponse() != null) {

                var output = new Output(resultListener.getResponse().response());
                LOGGER.info("RequestPartiesCommand : output : ({})", ObjectLogger.log(output));

                return output;
            }

            var error = resultListener.getError();
            var errorDefinition = FspiopErrors.find(
                error.errorInformation().getErrorInformation().getErrorCode());

            LOGGER.error("RequestPartiesCommand : error : ({})", ObjectLogger.log(error));

            throw new FspiopException(
                new ErrorDefinition(
                    errorDefinition.errorType(),
                    error.errorInformation().getErrorInformation().getErrorDescription()));
        } finally {

            LOGGER.info(
                "RequestPartiesCommand : done : took {} ms",
                (System.nanoTime() - startAt) / 1_000_000);

            MDC.remove("REQ_ID");
        }

    }

}

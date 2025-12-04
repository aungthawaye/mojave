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

import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.component.misc.pubsub.PubSubClient;
import io.mojaloop.connector.gateway.component.PubSubKeys;
import io.mojaloop.connector.gateway.data.PartiesErrorResult;
import io.mojaloop.connector.gateway.data.PartiesResult;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.gateway.outbound.component.FspiopResultListener;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payee;
import io.mojaloop.fspiop.invoker.api.parties.GetParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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

        LOGGER.info("RequestPartiesCommand : input : ({})", ObjectLogger.log(input));

        assert input != null;

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
            this.pubSubClient, this.outboundSettings,
            PartiesResult.class, PartiesErrorResult.class);

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

    }

}

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
import io.mojaloop.connector.gateway.data.TransfersErrorResult;
import io.mojaloop.connector.gateway.data.TransfersResult;
import io.mojaloop.connector.gateway.outbound.ConnectorOutboundConfiguration;
import io.mojaloop.connector.gateway.outbound.component.FspiopResultListener;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.invoker.api.transfers.PostTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class RequestTransfersCommandHandler implements RequestTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RequestTransfersCommandHandler.class.getName());

    private final PostTransfers postTransfers;

    private final PubSubClient pubSubClient;

    private final ConnectorOutboundConfiguration.OutboundSettings outboundSettings;

    public RequestTransfersCommandHandler(PostTransfers postTransfers, PubSubClient pubSubClient, ConnectorOutboundConfiguration.OutboundSettings outboundSettings) {

        assert null != postTransfers;
        assert null != pubSubClient;
        assert null != outboundSettings;

        this.postTransfers = postTransfers;
        this.pubSubClient = pubSubClient;
        this.outboundSettings = outboundSettings;
    }

    @Override
    public Output execute(Input input) throws FspiopException {

        LOGGER.info("Invoking PostTransfers : {}", input);

        assert input != null;
        assert input.request() != null;

        var transferId = input.request().getTransferId();
        var resultTopic = PubSubKeys.forTransfers(transferId);
        var errorTopic = PubSubKeys.forTransfersError(transferId);

        // Listening to the pub/sub
        var resultListener = new FspiopResultListener<>(this.pubSubClient, this.outboundSettings, TransfersResult.class, TransfersErrorResult.class);
        resultListener.init(resultTopic, errorTopic);

        this.postTransfers.postTransfers(input.payee(), input.request());

        resultListener.await();

        if (resultListener.getResponse() != null) {
            return new Output(resultListener.getResponse());
        }

        var error = resultListener.getError();
        var errorDefinition = FspiopErrors.find(error.errorInformation().getErrorInformation().getErrorCode());

        throw new FspiopException(new ErrorDefinition(errorDefinition.errorType(), error.errorInformation().getErrorInformation().getErrorDescription()));

    }

}

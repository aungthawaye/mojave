/*-
 * ================================================================================
 * Mojaloop OSS
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
package io.mojaloop.connector.gateway.inbound.command.quotes;

import io.mojaloop.connector.adapter.fsp.FspCoreAdapter;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Destination;
import io.mojaloop.fspiop.invoker.api.quotes.PutQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
class HandleQuotesRequestCommandHandler implements HandleQuotesRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(HandleQuotesRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutQuotes putQuotes;

    public HandleQuotesRequestCommandHandler(FspCoreAdapter fspCoreAdapter, PutQuotes putQuotes) {

        assert fspCoreAdapter != null;
        assert putQuotes != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putQuotes = putQuotes;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        var destination = new Destination(input.source().sourceFspCode());

        try {

            LOGGER.info("Calling FSP adapter to get quote for : {}", input);
            var response = this.fspCoreAdapter.postQuotes(input.source(), input.request());
            LOGGER.info("FSP adapter returned quote : {}", response);

            LOGGER.info("Responding the result to Hub : {}", response);
            this.putQuotes.putQuotes(destination, input.quoteId(), response);
            LOGGER.info("Responded the result to Hub");

        } catch (FspiopException e) {

            this.putQuotes.putQuotesError(destination, input.quoteId(), e.toErrorObject());
        }
    }

}

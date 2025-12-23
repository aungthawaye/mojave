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
package org.mojave.connector.gateway.inbound.command.quotes;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.connector.adapter.fsp.FspCoreAdapter;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.fspiop.invoker.api.quotes.PutQuotes;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
class HandlePostQuotesRequestCommandHandler implements HandlePostQuotesRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePostQuotesRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutQuotes putQuotes;

    public HandlePostQuotesRequestCommandHandler(FspCoreAdapter fspCoreAdapter,
                                                 PutQuotes putQuotes) {

        assert fspCoreAdapter != null;
        assert putQuotes != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putQuotes = putQuotes;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        MDC.put("REQ_ID", input.quoteId());

        var startAt = System.nanoTime();
        var endAt = 0L;

        final var payer = new Payer(input.payer().fspCode());

        try {

            LOGGER.info(
                "HandlePostQuotesRequestCommandHandler : input : ({})", ObjectLogger.log(input));
            final var response = this.fspCoreAdapter.postQuotes(payer, input.request());
            LOGGER.info(
                "HandlePostQuotesRequestCommandHandler : FSP Core : response : ({})",
                ObjectLogger.log(response));

            this.putQuotes.putQuotes(payer, input.quoteId(), response);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            this.putQuotes.putQuotesError(payer, input.quoteId(), e.toErrorObject());

        } finally {

            endAt = System.nanoTime();
            LOGGER.info(
                "HandlePostQuotesRequestCommandHandler : done : took {} ms",
                (endAt - startAt) / 1000000);

            MDC.remove("REQ_ID");
        }
    }

}

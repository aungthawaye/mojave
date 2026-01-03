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
package org.mojave.connector.gateway.inbound.command.parties;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.connector.adapter.fsp.FspCoreAdapter;
import org.mojave.rail.fspiop.component.exception.FspiopException;
import org.mojave.rail.fspiop.component.type.Payer;
import org.mojave.rail.fspiop.invoker.api.parties.PutParties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
class HandleGetPartiesRequestCommandHandler implements HandleGetPartiesRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandleGetPartiesRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutParties putParties;

    public HandleGetPartiesRequestCommandHandler(FspCoreAdapter fspCoreAdapter,
                                                 PutParties putParties) {

        assert fspCoreAdapter != null;
        assert putParties != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putParties = putParties;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        final var reqId =
            input.payer().fspCode() + "-" + input.partyIdType() + "-" + input.partyId();

        MDC.put("REQ_ID", reqId);

        var startAt = System.nanoTime();
        var endAt = 0L;

        var payer = new Payer(input.payer().fspCode());
        var hasSubId = input.subId() != null;

        try {

            LOGGER.info(
                "HandleGetPartiesRequestCommandHandler : input : ({})", ObjectLogger.log(input));
            var response = this.fspCoreAdapter.getParties(
                payer, input.partyIdType(), input.partyId(), input.subId());
            LOGGER.info(
                "HandleGetPartiesRequestCommandHandler : FSP Core : response : ({})",
                ObjectLogger.log(response));

            if (hasSubId) {
                this.putParties.putParties(
                    payer, input.partyIdType(), input.partyId(), input.subId(), response);
            } else {
                this.putParties.putParties(payer, input.partyIdType(), input.partyId(), response);
            }

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);

            if (hasSubId) {
                this.putParties.putPartiesError(
                    payer, input.partyIdType(), input.partyId(), input.subId(), e.toErrorObject());
            } else {
                this.putParties.putPartiesError(
                    payer, input.partyIdType(), input.partyId(), e.toErrorObject());
            }

        } finally {

            endAt = System.nanoTime();
            LOGGER.info(
                "HandleGetPartiesRequestCommandHandler : done : took {} ms",
                (endAt - startAt) / 1000000);

            MDC.remove("REQ_ID");
        }

    }

}

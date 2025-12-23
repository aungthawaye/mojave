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
package org.mojave.connector.gateway.inbound.command.transfers;

import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.connector.adapter.fsp.FspCoreAdapter;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.invoker.api.transfers.PutTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.slf4j.MDC;
import org.springframework.stereotype.Service;

@Service
class HandlePostTransfersRequestCommandHandler implements HandlePostTransfersRequestCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePostTransfersRequestCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    private final PutTransfers putTransfers;

    public HandlePostTransfersRequestCommandHandler(FspCoreAdapter fspCoreAdapter,
                                                    PutTransfers putTransfers) {

        assert fspCoreAdapter != null;
        assert putTransfers != null;

        this.fspCoreAdapter = fspCoreAdapter;
        this.putTransfers = putTransfers;
    }

    @Override
    public void execute(Input input) throws FspiopException {

        MDC.put("REQ_ID", input.transferId());

        var startAt = System.nanoTime();
        var endAt = 0L;

        final var payer = input.payer();

        try {

            LOGGER.info(
                "HandlePostTransfersRequestCommandHandler : input : ({})", ObjectLogger.log(input));
            final var response = this.fspCoreAdapter.postTransfers(payer, input.request());
            LOGGER.info(
                "HandlePostTransfersRequestCommandHandler : FSP Core : response : ({})",
                ObjectLogger.log(response));

            this.putTransfers.putTransfers(payer, input.transferId(), response);

        } catch (FspiopException e) {

            LOGGER.error("Error:", e);
            this.putTransfers.putTransfersError(payer, input.transferId(), e.toErrorObject());

        } finally {

            endAt = System.nanoTime();
            LOGGER.info(
                "HandlePostTransfersRequestCommandHandler : done : took {} ms",
                (endAt - startAt) / 1000000);

            MDC.remove("REQ_ID");
        }
    }

}

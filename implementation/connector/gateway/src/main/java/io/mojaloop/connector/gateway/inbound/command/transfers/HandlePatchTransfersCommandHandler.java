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

package io.mojaloop.connector.gateway.inbound.command.transfers;

import io.mojaloop.connector.adapter.fsp.FspCoreAdapter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class HandlePatchTransfersCommandHandler implements HandlePatchTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        HandlePatchTransfersCommandHandler.class.getName());

    private final FspCoreAdapter fspCoreAdapter;

    public HandlePatchTransfersCommandHandler(FspCoreAdapter fspCoreAdapter) {

        assert null != fspCoreAdapter;

        this.fspCoreAdapter = fspCoreAdapter;
    }

    @Override
    public HandlePatchTransfersCommand.Output execute(HandlePatchTransfersCommand.Input input) {

        try {

            LOGGER.info("Calling FSP adapter to initiate transfer for : {}", input);
            this.fspCoreAdapter.patchTransfers(input.payer(), input.transferId(), input.response());
            LOGGER.info("Done calling FSP adapter to initiate transfer for : {}", input);

        } catch (Exception e) {

            LOGGER.error("Error handling PatchTransfers", e);
        }

        return null;
    }

}

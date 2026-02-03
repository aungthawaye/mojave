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
package org.mojave.rail.fspiop.transfer.domain.command;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.participant.store.ParticipantStore;
import org.mojave.rail.fspiop.transfer.domain.TransferDomainConfiguration;
import org.mojave.rail.fspiop.transfer.contract.command.GetTransfersCommand;
import org.mojave.rail.fspiop.transfer.domain.repository.TransferRepository;
import org.mojave.rail.fspiop.bootstrap.api.forwarder.ForwardRequest;
import org.mojave.rail.fspiop.bootstrap.api.transfers.RespondTransfers;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.PlatformTransactionManager;
import java.util.Objects;

@Service
public class GetTransfersCommandHandler implements GetTransfersCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetTransfersCommandHandler.class);

    private final ParticipantStore participantStore;

    private final RespondTransfers respondTransfers;

    private final ForwardRequest forwardRequest;

    private final TransferRepository transferRepository;

    private final PlatformTransactionManager transactionManager;

    private final TransferDomainConfiguration.TransferSettings transferSettings;

    public GetTransfersCommandHandler(ParticipantStore participantStore,
                                      RespondTransfers respondTransfers,
                                      ForwardRequest forwardRequest,
                                      TransferRepository transferRepository,
                                      PlatformTransactionManager transactionManager,
                                      TransferDomainConfiguration.TransferSettings transferSettings) {

        Objects.requireNonNull(participantStore);
        Objects.requireNonNull(respondTransfers);
        Objects.requireNonNull(forwardRequest);
        Objects.requireNonNull(transferRepository);
        Objects.requireNonNull(transactionManager);
        Objects.requireNonNull(transferSettings);

        this.participantStore = participantStore;
        this.respondTransfers = respondTransfers;
        this.forwardRequest = forwardRequest;
        this.transferRepository = transferRepository;
        this.transactionManager = transactionManager;
        this.transferSettings = transferSettings;
    }

    @Override
    @Read
    public Output execute(Input input) {

        var udfTransferId = input.udfTransferId();

        LOGGER.info("Executing GetTransfersCommandHandler with input: {}", input);

        return new Output();
    }

}

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
package org.mojave.core.wallet.domain.command.position;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.position.CreatePositionCommand;
import org.mojave.core.wallet.contract.exception.position.PositionAlreadyExistsException;
import org.mojave.core.wallet.domain.model.Position;
import org.mojave.core.wallet.domain.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePositionCommandHandler implements CreatePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreatePositionCommandHandler.class);

    private final PositionRepository positionRepository;

    public CreatePositionCommandHandler(final PositionRepository positionRepository) {

        assert positionRepository != null;
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreatePositionCommand : input: ({})", ObjectLogger.log(input));

        // Build uniqueness spec: walletOwnerId + currency
        final var spec = PositionRepository.Filters
                             .withOwnerId(input.walletOwnerId())
                             .and(PositionRepository.Filters.withCurrency(input.currency()));

        if (this.positionRepository.findOne(spec).isPresent()) {
            throw new PositionAlreadyExistsException(input.walletOwnerId(), input.currency());
        }

        final var position = new Position(
            input.walletOwnerId(), input.currency(), input.name(), input.netDebitCap());

        final var saved = this.positionRepository.save(position);

        final var output = new Output(saved.getId());

        LOGGER.info("CreatePositionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

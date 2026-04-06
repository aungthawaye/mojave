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

package org.mojave.accounting.domain.command.definition;

import org.mojave.accounting.contract.command.definition.AddFlowLineCommand;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AddFlowLineCommandHandler implements AddFlowLineCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFlowLineCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AccountCache accountCache;

    private final CoaEntryCache coaEntryCache;

    public AddFlowLineCommandHandler(final FlowDefinitionRepository flowDefinitionRepository,
                                     final AccountCache accountCache,
                                     final CoaEntryCache coaEntryCache) {

        Objects.requireNonNull(flowDefinitionRepository);
        Objects.requireNonNull(accountCache);
        Objects.requireNonNull(coaEntryCache);

        this.flowDefinitionRepository = flowDefinitionRepository;
        this.accountCache = accountCache;
        this.coaEntryCache = coaEntryCache;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("AddFlowLineCommand : input: ({})", ObjectLogger.log(input));

        final var definition = this.flowDefinitionRepository
                                   .findById(input.flowDefinitionId())
                                   .orElseThrow(() -> new FlowDefinitionNotFoundException(
                                       input.flowDefinitionId()));
        final var flowLine = input.flowLine();

        final var savedFlowLine = definition.addFlowLine(
            flowLine.step(), flowLine.participant(), flowLine.coaEntryId(), flowLine.amountName(),
            flowLine.side(), flowLine.description(), this.accountCache, this.coaEntryCache);

        this.flowDefinitionRepository.save(definition);
        final var output = new Output(definition.getId(), savedFlowLine.getId());

        LOGGER.info("AddFlowLineCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

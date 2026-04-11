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

import org.mojave.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.model.FlowDefinition;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionLineId;
import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class CreateFlowDefinitionCommandHandler implements CreateFlowDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateFlowDefinitionCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AccountCache accountCache;

    private final CoaEntryCache coaEntryCache;

    public CreateFlowDefinitionCommandHandler(final FlowDefinitionRepository flowDefinitionRepository,
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

        LOGGER.info("CreateFlowDefinitionCommand : input: ({})", ObjectLogger.log(input));

        final var scenario = input.scenario();
        final var currency = input.currency();

        final var withScenario = FlowDefinitionRepository.Filters.withScenario(scenario);
        final var withCurrency = FlowDefinitionRepository.Filters.withCurrency(currency);

        if (this.flowDefinitionRepository.findOne(withScenario.and(withCurrency)).isPresent()) {
            throw new FlowDefinitionAlreadyConfiguredException(scenario, currency);
        }

        if (this.flowDefinitionRepository
                .findOne(FlowDefinitionRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new FlowDefinitionNameTakenException(input.name());
        }

        var definition = new FlowDefinition(
            input.scenario(), currency, input.name(), input.description());

        final var flowDefinitionLineIds = new ArrayList<FlowDefinitionLineId>();

        for (final var flowDefinitionLine : input.flowDefinitionLines()) {

            final var savedFlowDefinitionLine = definition.addFlowDefinitionLine(
                flowDefinitionLine.step(), flowDefinitionLine.participant(), flowDefinitionLine.coaEntryId(),
                flowDefinitionLine.amountName(), flowDefinitionLine.side(), flowDefinitionLine.description(), this.accountCache,
                this.coaEntryCache);
            flowDefinitionLineIds.add(savedFlowDefinitionLine.getId());
        }

        definition = this.flowDefinitionRepository.save(definition);
        final var output = new Output(definition.getId(), flowDefinitionLineIds);

        LOGGER.info("CreateFlowDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

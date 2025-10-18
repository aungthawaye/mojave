/*-
 * =============================================================================
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
 * =============================================================================
 */

package io.mojaloop.core.accounting.domain.command.definition;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.accounting.contract.command.definition.AddFlowDefinitionPostingCommand;
import io.mojaloop.core.accounting.contract.exception.definition.ChartEntryConflictsInPostingDefinitionException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.model.PostingDefinition;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddFlowDefinitionPostingCommandHandler implements AddFlowDefinitionPostingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFlowDefinitionPostingCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    public AddFlowDefinitionPostingCommandHandler(final FlowDefinitionRepository flowDefinitionRepository,
                                                  @Qualifier(AccountCache.Qualifiers.DEFAULT) final AccountCache accountCache,
                                                  @Qualifier(ChartEntryCache.Qualifiers.DEFAULT) final ChartEntryCache chartEntryCache) {

        assert flowDefinitionRepository != null;
        assert accountCache != null;
        assert chartEntryCache != null;

        this.flowDefinitionRepository = flowDefinitionRepository;
        this.accountCache = accountCache;
        this.chartEntryCache = chartEntryCache;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("Executing AddFlowDefinitionPostingCommand with input: {}", input);

        final var definition = this.flowDefinitionRepository.findById(input.flowDefinitionId())
                                                            .orElseThrow(() -> new FlowDefinitionNotFoundException(input.flowDefinitionId()));

        for (final var posting : input.postings()) {
            LOGGER.info("Adding posting: {}", posting);

            final var pd = new PostingDefinition(
                definition,
                posting.participantType(),
                posting.amountName(),
                posting.side(),
                posting.selection(),
                posting.selectedId(),
                posting.description(),
                this.accountCache,
                this.chartEntryCache
            );

            definition.getPostingDefinitions().add(pd);
        }

        this.flowDefinitionRepository.save(definition);

        LOGGER.info("Completed AddFlowDefinitionPostingCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

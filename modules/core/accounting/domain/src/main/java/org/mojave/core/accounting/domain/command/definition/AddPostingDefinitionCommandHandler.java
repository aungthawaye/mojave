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

package org.mojave.core.accounting.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.accounting.contract.command.definition.AddPostingDefinitionCommand;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AddPostingDefinitionCommandHandler implements AddPostingDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        AddPostingDefinitionCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    public AddPostingDefinitionCommandHandler(final FlowDefinitionRepository flowDefinitionRepository,
                                              final AccountCache accountCache,
                                              final ChartEntryCache chartEntryCache) {

        Objects.requireNonNull(flowDefinitionRepository);
        Objects.requireNonNull(accountCache);
        Objects.requireNonNull(chartEntryCache);

        this.flowDefinitionRepository = flowDefinitionRepository;
        this.accountCache = accountCache;
        this.chartEntryCache = chartEntryCache;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("AddPostingDefinitionCommand : input: ({})", ObjectLogger.log(input));

        final var definition = this.flowDefinitionRepository
                                   .findById(input.flowDefinitionId())
                                   .orElseThrow(() -> new FlowDefinitionNotFoundException(
                                       input.flowDefinitionId()));
        final var posting = input.posting();

        var pd = definition.addPosting(
            input.posting().step(), posting.receiveIn(), posting.receiveInId(),
            posting.participant(), posting.amountName(), posting.side(), posting.description(),
            this.accountCache, this.chartEntryCache);

        this.flowDefinitionRepository.save(definition);
        var output = new Output(definition.getId(), pd.getId());

        LOGGER.info("AddPostingDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

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
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.core.accounting.domain.cache.AccountCache;
import org.mojave.core.accounting.domain.cache.ChartEntryCache;
import org.mojave.core.accounting.domain.model.FlowDefinition;
import org.mojave.core.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.common.datatype.identifier.accounting.PostingDefinitionId;
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

    private final ChartEntryCache chartEntryCache;

    public CreateFlowDefinitionCommandHandler(final FlowDefinitionRepository flowDefinitionRepository,
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

        LOGGER.info("CreateFlowDefinitionCommand : input: ({})", ObjectLogger.log(input));

        final var transactionType = input.transactionType();
        final var currency = input.currency();

        final var withTransactionType = FlowDefinitionRepository.Filters.withTransactionType(
            transactionType);
        final var withCurrency = FlowDefinitionRepository.Filters.withCurrency(currency);

        if (this.flowDefinitionRepository
                .findOne(withTransactionType.and(withCurrency))
                .isPresent()) {
            throw new FlowDefinitionAlreadyConfiguredException(transactionType, currency);
        }

        if (this.flowDefinitionRepository
                .findOne(FlowDefinitionRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new FlowDefinitionNameTakenException(input.name());
        }

        var definition = new FlowDefinition(
            input.transactionType(), currency, input.name(), input.description());

        final var postingIds = new ArrayList<PostingDefinitionId>();

        for (final var posting : input.postings()) {

            final var pd = definition.addPosting(
                posting.step(), posting.receiveIn(), posting.receiveInId(), posting.participant(),
                posting.amountName(), posting.side(), posting.description(), this.accountCache,
                this.chartEntryCache);
            postingIds.add(pd.getId());
        }

        definition = this.flowDefinitionRepository.save(definition);
        var output = new Output(definition.getId(), postingIds);

        LOGGER.info("CreateFlowDefinitionCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

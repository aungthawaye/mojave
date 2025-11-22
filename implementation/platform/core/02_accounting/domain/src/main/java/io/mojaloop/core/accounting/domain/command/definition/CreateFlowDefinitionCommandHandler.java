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
/*-
 * =============================================================================
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
 * =============================================================================
 */

package io.mojaloop.core.accounting.domain.command.definition;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionWithCurrencyExistsException;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.ChartEntryCache;
import io.mojaloop.core.accounting.domain.model.FlowDefinition;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.PostingDefinitionId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CreateFlowDefinitionCommandHandler implements CreateFlowDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFlowDefinitionCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final AccountCache accountCache;

    private final ChartEntryCache chartEntryCache;

    public CreateFlowDefinitionCommandHandler(final FlowDefinitionRepository flowDefinitionRepository, final AccountCache accountCache, final ChartEntryCache chartEntryCache) {

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

        LOGGER.info("Executing CreateFlowDefinitionCommand with input: {}", input);

        final var currency = input.currency();

        if (this.flowDefinitionRepository.findOne(FlowDefinitionRepository.Filters.withCurrency(currency)).isPresent()) {
            LOGGER.info("Flow Definition with currency {} already exists", currency);
            throw new FlowDefinitionWithCurrencyExistsException(currency);
        }

        if (this.flowDefinitionRepository.findOne(FlowDefinitionRepository.Filters.withNameEquals(input.name())).isPresent()) {
            LOGGER.info("Flow Definition with name {} already exists", input.name());
            throw new FlowDefinitionNameTakenException(input.name());
        }

        var definition = new FlowDefinition(input.transactionType(), currency, input.name(), input.description());
        LOGGER.info("Created Flow Definition: {}", definition);

        final var postingIds = new ArrayList<PostingDefinitionId>();

        for (final var posting : input.postings()) {

            LOGGER.info("Adding posting: {}", posting);

            final var pd = definition.addPosting(posting.receiveIn(), posting.receiveInId(), posting.participant(), posting.amountName(), posting.side(), posting.description(),
                this.accountCache, this.chartEntryCache);
            postingIds.add(pd.getId());
        }

        definition = this.flowDefinitionRepository.save(definition);

        LOGGER.info("Completed CreateFlowDefinitionCommand with input: {}", input);

        return new Output(definition.getId(), postingIds);
    }

}

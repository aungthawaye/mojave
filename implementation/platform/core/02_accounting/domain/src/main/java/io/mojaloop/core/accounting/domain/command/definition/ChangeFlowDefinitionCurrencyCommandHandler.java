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
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.accounting.contract.command.definition.ChangeFlowDefinitionCurrencyCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeFlowDefinitionCurrencyCommandHandler
    implements ChangeFlowDefinitionCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeFlowDefinitionCurrencyCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    public ChangeFlowDefinitionCurrencyCommandHandler(final FlowDefinitionRepository flowDefinitionRepository) {

        assert flowDefinitionRepository != null;

        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("ChangeFlowDefinitionCurrencyCommand : input: ({})", ObjectLogger.log(input));

        final var definition = this.flowDefinitionRepository
                                   .findById(input.flowDefinitionId())
                                   .orElseThrow(() -> new FlowDefinitionNotFoundException(
                                       input.flowDefinitionId()));

        // Ensure no other definition already uses the target currency
        final var transactionType = definition.getTransactionType();
        final var currency = input.currency();

        final var withTransactionType = FlowDefinitionRepository.Filters.withTransactionType(
            transactionType);
        final var withCurrency = FlowDefinitionRepository.Filters.withCurrency(currency);
        final var withIdNotEquals = FlowDefinitionRepository.Filters.withIdNotEquals(
            definition.getId());

        final var conflict = this.flowDefinitionRepository.findOne(
            withTransactionType.and(withCurrency).and(withIdNotEquals));

        if (conflict.isPresent()) {
            throw new FlowDefinitionAlreadyConfiguredException(transactionType, currency);
        }

        definition.currency(currency);

        this.flowDefinitionRepository.save(definition);
        var output = new Output(definition.getId());

        LOGGER.info("ChangeFlowDefinitionCurrencyCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

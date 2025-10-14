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

package io.mojaloop.core.transaction.domain.command.definition.fundin;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.transaction.contract.command.definition.fundin.ChangeFundInDefinitionCurrencyCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionWithCurrencyExistsException;
import io.mojaloop.core.transaction.domain.repository.definition.FundInDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeFundInDefinitionCurrencyCommandHandler implements ChangeFundInDefinitionCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundInDefinitionCurrencyCommandHandler.class);

    private final FundInDefinitionRepository fundInDefinitionRepository;

    public ChangeFundInDefinitionCurrencyCommandHandler(FundInDefinitionRepository fundInDefinitionRepository) {

        assert fundInDefinitionRepository != null;

        this.fundInDefinitionRepository = fundInDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws FundInDefinitionNotFoundException, FundInDefinitionWithCurrencyExistsException {

        LOGGER.info("Executing ChangeFundInDefinitionCurrencyCommand with input: {}", input);

        var definition = this.fundInDefinitionRepository.findById(input.definitionId())
                                                        .orElseThrow(() -> new FundInDefinitionNotFoundException(input.definitionId()));

        var duplicateFilter = FundInDefinitionRepository.Filters.withCurrency(input.currency())
                                                                .and(FundInDefinitionRepository.Filters.withIdNotEquals(input.definitionId()));

        if (this.fundInDefinitionRepository.findOne(duplicateFilter).isPresent()) {
            LOGGER.info("Fund-In Definition with currency {} already exists", input.currency());
            throw new FundInDefinitionWithCurrencyExistsException(input.currency());
        }

        definition.currency(input.currency());

        this.fundInDefinitionRepository.save(definition);

        LOGGER.info("Completed ChangeFundInDefinitionCurrencyCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

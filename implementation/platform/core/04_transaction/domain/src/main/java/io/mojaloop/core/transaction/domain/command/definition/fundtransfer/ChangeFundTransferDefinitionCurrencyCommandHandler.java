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

package io.mojaloop.core.transaction.domain.command.definition.fundtransfer;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.ChangeFundTransferDefinitionCurrencyCommand;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionWithCurrencyExistsException;
import io.mojaloop.core.transaction.domain.repository.definition.FundTransferDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class ChangeFundTransferDefinitionCurrencyCommandHandler implements ChangeFundTransferDefinitionCurrencyCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFundTransferDefinitionCurrencyCommandHandler.class);

    private final FundTransferDefinitionRepository fundTransferDefinitionRepository;

    public ChangeFundTransferDefinitionCurrencyCommandHandler(FundTransferDefinitionRepository fundTransferDefinitionRepository) {

        assert fundTransferDefinitionRepository != null;

        this.fundTransferDefinitionRepository = fundTransferDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws FundTransferDefinitionNotFoundException, FundTransferDefinitionWithCurrencyExistsException {

        LOGGER.info("Executing ChangeFundTransferDefinitionCurrencyCommand with input: {}", input);

        var definition = this.fundTransferDefinitionRepository.findById(input.definitionId())
                                                              .orElseThrow(() -> new FundTransferDefinitionNotFoundException(input.definitionId()));

        var duplicateFilter = FundTransferDefinitionRepository.Filters.withCurrency(input.currency())
                                                                      .and(FundTransferDefinitionRepository.Filters.withIdNotEquals(input.definitionId()));

        if (this.fundTransferDefinitionRepository.findOne(duplicateFilter).isPresent()) {
            LOGGER.info("Fund-Transfer Definition with currency {} already exists", input.currency());
            throw new FundTransferDefinitionWithCurrencyExistsException(input.currency());
        }

        definition.currency(input.currency());

        this.fundTransferDefinitionRepository.save(definition);

        LOGGER.info("Completed ChangeFundTransferDefinitionCurrencyCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

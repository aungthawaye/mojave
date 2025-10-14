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
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.CreateFundTransferDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionWithCurrencyExistsException;
import io.mojaloop.core.transaction.domain.model.definition.FundTransferDefinition;
import io.mojaloop.core.transaction.domain.repository.definition.FundTransferDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CreateFundTransferDefinitionCommandHandler implements CreateFundTransferDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundTransferDefinitionCommandHandler.class);

    private final FundTransferDefinitionRepository fundTransferDefinitionRepository;

    public CreateFundTransferDefinitionCommandHandler(FundTransferDefinitionRepository fundTransferDefinitionRepository) {

        assert fundTransferDefinitionRepository != null;

        this.fundTransferDefinitionRepository = fundTransferDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws PostingAlreadyExistsException,
            FundTransferDefinitionWithCurrencyExistsException,
            FundTransferDefinitionNameTakenException {

        LOGGER.info("Executing CreateFundTransferDefinitionCommand with input: {}", input);

        final var currency = input.currency();

        if (this.fundTransferDefinitionRepository.findOne(FundTransferDefinitionRepository.Filters.withCurrency(currency)).isPresent()) {
            LOGGER.info("Fund-Transfer Definition with currency {} already exists", currency);
            throw new FundTransferDefinitionWithCurrencyExistsException(currency);
        }

        if (this.fundTransferDefinitionRepository.findOne(FundTransferDefinitionRepository.Filters.withNameEquals(input.name())).isPresent()) {
            LOGGER.info("Fund-Transfer Definition with name {} already exists", input.name());
            throw new FundTransferDefinitionNameTakenException(input.name());
        }

        var definition = new FundTransferDefinition(currency, input.name(), input.description());
        LOGGER.info("Created Fund-Transfer Definition: {}", definition);

        final var postingIds = new ArrayList<PostingId>();

        if (input.postings() != null) {
            for (var posting : input.postings()) {
                LOGGER.info("Adding posting: {}", posting);
                var p = definition.addPosting(posting.forOwner(), posting.forAmount(), posting.side(), posting.chartEntryId(), null);
                postingIds.add(p.getId());
            }
        }

        definition = this.fundTransferDefinitionRepository.save(definition);

        LOGGER.info("Completed CreateFundTransferDefinitionCommand with input: {}", input);

        return new Output(definition.getId(), postingIds);
    }
}

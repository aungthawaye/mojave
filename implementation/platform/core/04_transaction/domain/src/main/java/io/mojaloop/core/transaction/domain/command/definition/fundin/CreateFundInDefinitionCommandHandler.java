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
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.command.definition.fundin.CreateFundInDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionWithCurrencyExistsException;
import io.mojaloop.core.transaction.domain.model.definition.FundInDefinition;
import io.mojaloop.core.transaction.domain.repository.definition.FundInDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CreateFundInDefinitionCommandHandler implements CreateFundInDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundInDefinitionCommandHandler.class);

    private final FundInDefinitionRepository fundInDefinitionRepository;

    public CreateFundInDefinitionCommandHandler(FundInDefinitionRepository fundInDefinitionRepository) {

        assert fundInDefinitionRepository != null;

        this.fundInDefinitionRepository = fundInDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws PostingAlreadyExistsException, FundInDefinitionWithCurrencyExistsException, FundInDefinitionNameTakenException {

        LOGGER.info("Executing CreateFundInDefinitionCommand with input: {}", input);

        final var currency = input.currency();

        if (this.fundInDefinitionRepository.findOne(FundInDefinitionRepository.Filters.withCurrency(currency)).isPresent()) {
            LOGGER.info("Fund-In Definition with currency {} already exists", currency);
            throw new FundInDefinitionWithCurrencyExistsException(currency);
        }

        if (this.fundInDefinitionRepository.findOne(FundInDefinitionRepository.Filters.withNameEquals(input.name())).isPresent()) {
            LOGGER.info("Fund-In Definition with name {} already exists", input.name());
            throw new FundInDefinitionNameTakenException(input.name());
        }

        var fundIn = new FundInDefinition(currency, input.name(), input.description());
        LOGGER.info("Created Fund-In Definition: {}", fundIn);

        final var postingIds = new ArrayList<PostingId>();

        for (var posting : input.postings()) {
            LOGGER.info("Adding posting: {}", posting);
            var p = fundIn.addPosting(posting.forOwner(), posting.forAmount(), posting.side(), posting.chartEntryId(), null);
            postingIds.add(p.getId());
        }

        fundIn = this.fundInDefinitionRepository.save(fundIn);

        LOGGER.info("Completed CreateFundInDefinitionCommand with input: {}", input);

        return new Output(fundIn.getId(), postingIds);
    }

}

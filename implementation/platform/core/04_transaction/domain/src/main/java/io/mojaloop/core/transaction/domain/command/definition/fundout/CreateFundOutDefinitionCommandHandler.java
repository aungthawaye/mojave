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

package io.mojaloop.core.transaction.domain.command.definition.fundout;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.common.datatype.identifier.transaction.PostingId;
import io.mojaloop.core.transaction.contract.command.definition.fundout.CreateFundOutDefinitionCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNameTakenException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionWithCurrencyExistsException;
import io.mojaloop.core.transaction.domain.model.definition.FundOutDefinition;
import io.mojaloop.core.transaction.domain.repository.definition.FundOutDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;

@Service
public class CreateFundOutDefinitionCommandHandler implements CreateFundOutDefinitionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFundOutDefinitionCommandHandler.class);

    private final FundOutDefinitionRepository fundOutDefinitionRepository;

    public CreateFundOutDefinitionCommandHandler(FundOutDefinitionRepository fundOutDefinitionRepository) {

        assert fundOutDefinitionRepository != null;

        this.fundOutDefinitionRepository = fundOutDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws PostingAlreadyExistsException, FundOutDefinitionWithCurrencyExistsException, FundOutDefinitionNameTakenException {

        LOGGER.info("Executing CreateFundOutDefinitionCommand with input: {}", input);

        final var currency = input.currency();

        if (this.fundOutDefinitionRepository.findOne(FundOutDefinitionRepository.Filters.withCurrency(currency)).isPresent()) {
            LOGGER.info("Fund-Out Definition with currency {} already exists", currency);
            throw new FundOutDefinitionWithCurrencyExistsException(currency);
        }

        if (this.fundOutDefinitionRepository.findOne(FundOutDefinitionRepository.Filters.withNameEquals(input.name())).isPresent()) {
            LOGGER.info("Fund-Out Definition with name {} already exists", input.name());
            throw new FundOutDefinitionNameTakenException(input.name());
        }

        var fundOut = new FundOutDefinition(currency, input.name(), input.description());
        LOGGER.info("Created Fund-Out Definition: {}", fundOut);

        final var postingIds = new ArrayList<PostingId>();

        if (input.postings() != null) {
            for (var posting : input.postings()) {
                LOGGER.info("Adding posting: {}", posting);
                var p = fundOut.addPosting(posting.forOwner(), posting.forAmount(), posting.side(), posting.chartEntryId(), null);
                postingIds.add(p.getId());
            }
        }

        fundOut = this.fundOutDefinitionRepository.save(fundOut);

        LOGGER.info("Completed CreateFundOutDefinitionCommand with input: {}", input);

        return new Output(fundOut.getId(), postingIds);
    }
}

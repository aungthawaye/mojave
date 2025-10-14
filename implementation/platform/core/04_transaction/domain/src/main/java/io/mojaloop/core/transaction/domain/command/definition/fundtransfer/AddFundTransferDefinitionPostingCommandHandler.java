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
import io.mojaloop.core.transaction.contract.command.definition.fundtransfer.AddFundTransferDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.PostingAlreadyExistsException;
import io.mojaloop.core.transaction.contract.exception.fundtransfer.FundTransferDefinitionNotFoundException;
import io.mojaloop.core.transaction.domain.repository.definition.FundTransferDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddFundTransferDefinitionPostingCommandHandler implements AddFundTransferDefinitionPostingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFundTransferDefinitionPostingCommandHandler.class);

    private final FundTransferDefinitionRepository fundTransferDefinitionRepository;

    public AddFundTransferDefinitionPostingCommandHandler(FundTransferDefinitionRepository fundTransferDefinitionRepository) {

        assert fundTransferDefinitionRepository != null;

        this.fundTransferDefinitionRepository = fundTransferDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws PostingAlreadyExistsException, FundTransferDefinitionNotFoundException {

        LOGGER.info("Executing AddFundTransferDefinitionPostingCommand with input: {}", input);

        var definition = this.fundTransferDefinitionRepository.findById(input.definitionId())
                                                              .orElseThrow(() -> new FundTransferDefinitionNotFoundException(input.definitionId()));

        if (input.postings() != null) {
            for (var posting : input.postings()) {
                definition.addPosting(posting.forOwner(), posting.forAmount(), posting.side(), posting.chartEntryId(), null);
            }
        }

        this.fundTransferDefinitionRepository.save(definition);

        LOGGER.info("Completed AddFundTransferDefinitionPostingCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

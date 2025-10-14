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
import io.mojaloop.core.transaction.contract.command.definition.fundout.RemoveFundOutDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundout.FundOutDefinitionPostingNotFoundException;
import io.mojaloop.core.transaction.domain.repository.definition.FundOutDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveFundOutDefinitionPostingCommandHandler implements RemoveFundOutDefinitionPostingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFundOutDefinitionPostingCommandHandler.class);

    private final FundOutDefinitionRepository fundOutDefinitionRepository;

    public RemoveFundOutDefinitionPostingCommandHandler(FundOutDefinitionRepository fundOutDefinitionRepository) {

        assert fundOutDefinitionRepository != null;

        this.fundOutDefinitionRepository = fundOutDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws FundOutDefinitionPostingNotFoundException, FundOutDefinitionNotFoundException {

        LOGGER.info("Executing RemoveFundOutDefinitionPostingCommand with input: {}", input);

        var definition = this.fundOutDefinitionRepository.findById(input.definitionId())
                                                         .orElseThrow(() -> new FundOutDefinitionNotFoundException(input.definitionId()));

        definition.removePosting(input.postingId());

        this.fundOutDefinitionRepository.save(definition);

        LOGGER.info("Completed RemoveFundOutDefinitionPostingCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

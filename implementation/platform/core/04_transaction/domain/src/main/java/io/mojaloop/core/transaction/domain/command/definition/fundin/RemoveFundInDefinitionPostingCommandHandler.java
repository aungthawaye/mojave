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
import io.mojaloop.core.transaction.contract.command.definition.fundin.RemoveFundInDefinitionPostingCommand;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionNotFoundException;
import io.mojaloop.core.transaction.contract.exception.fundin.FundInDefinitionPostingNotFoundException;
import io.mojaloop.core.transaction.domain.repository.definition.FundInDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveFundInDefinitionPostingCommandHandler implements RemoveFundInDefinitionPostingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFundInDefinitionPostingCommandHandler.class);

    private final FundInDefinitionRepository fundInDefinitionRepository;

    public RemoveFundInDefinitionPostingCommandHandler(FundInDefinitionRepository fundInDefinitionRepository) {

        assert fundInDefinitionRepository != null;

        this.fundInDefinitionRepository = fundInDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) throws FundInDefinitionPostingNotFoundException, FundInDefinitionNotFoundException {

        LOGGER.info("Executing RemoveFundInDefinitionPostingCommand with input: {}", input);

        var definition = this.fundInDefinitionRepository.findById(input.definitionId())
                                                        .orElseThrow(() -> new FundInDefinitionNotFoundException(input.definitionId()));

        definition.removePosting(input.postingId());

        this.fundInDefinitionRepository.save(definition);

        LOGGER.info("Completed RemoveFundInDefinitionPostingCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

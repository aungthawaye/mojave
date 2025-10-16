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

package io.mojaloop.core.accounting.domain.command.definition;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.accounting.contract.command.definition.RemoveFlowDefinitionPostingCommand;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.exception.definition.PostingDefinitionNotFoundException;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class RemoveFlowDefinitionPostingCommandHandler implements RemoveFlowDefinitionPostingCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(RemoveFlowDefinitionPostingCommandHandler.class);

    private final FlowDefinitionRepository flowDefinitionRepository;

    public RemoveFlowDefinitionPostingCommandHandler(final FlowDefinitionRepository flowDefinitionRepository) {

        assert flowDefinitionRepository != null;

        this.flowDefinitionRepository = flowDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) throws PostingDefinitionNotFoundException, FlowDefinitionNotFoundException {

        LOGGER.info("Executing RemoveFlowDefinitionPostingCommand with input: {}", input);

        final var definition = this.flowDefinitionRepository.findById(input.flowDefinitionId())
                                                            .orElseThrow(() -> new FlowDefinitionNotFoundException(input.flowDefinitionId()));

        definition.removePosting(input.postingDefinitionId());

        this.flowDefinitionRepository.save(definition);

        LOGGER.info("Completed RemoveFlowDefinitionPostingCommand with input: {}", input);

        return new Output(definition.getId());
    }
}

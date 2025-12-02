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

package io.mojaloop.core.transaction.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.transaction.contract.command.AddStepCommand;
import io.mojaloop.core.transaction.contract.exception.TransactionIdNotFoundException;
import io.mojaloop.core.transaction.domain.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class AddStepCommandHandler implements AddStepCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddStepCommandHandler.class);

    private final TransactionRepository transactionRepository;

    public AddStepCommandHandler(TransactionRepository transactionRepository) {

        assert transactionRepository != null;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("AddStepCommand : input: ({})", ObjectLogger.log(input));

        var transaction = this.transactionRepository
                              .findById(input.transactionId())
                              .orElseThrow(
                                  () -> new TransactionIdNotFoundException(input.transactionId()));

        transaction.addStep(input.phase(), input.name(), input.context(), input.payload());

        this.transactionRepository.save(transaction);

        var output = new Output(transaction.getId());
        LOGGER.info("AddStepCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

/*-
 * ================================================================================
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
 * ================================================================================
 */

package io.mojaloop.core.transaction.domain.command;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.command.OpenTransactionCommand;
import io.mojaloop.core.transaction.domain.model.Transaction;
import io.mojaloop.core.transaction.domain.repository.TransactionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OpenTransactionCommandHandler implements OpenTransactionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(OpenTransactionCommandHandler.class);

    private final TransactionRepository transactionRepository;

    public OpenTransactionCommandHandler(TransactionRepository transactionRepository) {

        assert transactionRepository != null;
        this.transactionRepository = transactionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("Executing OpenTransactionCommand with input: {}", input);

        var transactionId = new TransactionId(Snowflake.get().nextId());
        var transaction = new Transaction(transactionId, input.type());
        LOGGER.info("Created Transaction: {}", transactionId);

        transaction = this.transactionRepository.save(transaction);
        LOGGER.info("Saved Transaction with id: {}", transaction.getId());

        LOGGER.info("Completed OpenTransactionCommand with input: {}", input);

        return new Output(transaction.getId());
    }
}

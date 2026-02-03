/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.wallet.domain.command.balance;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.wallet.contract.command.balance.CreateBalanceCommand;
import org.mojave.core.wallet.contract.exception.balance.BalanceAlreadyExistsException;
import org.mojave.core.wallet.domain.model.Balance;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.Objects;

@Service
public class CreateBalanceCommandHandler implements CreateBalanceCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateBalanceCommandHandler.class);

    private final BalanceRepository balanceRepository;

    public CreateBalanceCommandHandler(BalanceRepository balanceRepository) {

        Objects.requireNonNull(balanceRepository);
        this.balanceRepository = balanceRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(Input input) {

        LOGGER.info("CreateBalanceCommand : input: ({})", ObjectLogger.log(input));

        var spec = BalanceRepository.Filters
                       .withOwnerId(input.walletOwnerId())
                       .and(BalanceRepository.Filters.withCurrency(input.currency()));

        if (this.balanceRepository.findOne(spec).isPresent()) {
            throw new BalanceAlreadyExistsException(input.walletOwnerId(), input.currency());
        }

        var wallet = new Balance(input.walletOwnerId(), input.currency(), input.name());

        wallet = this.balanceRepository.save(wallet);

        var output = new Output(wallet.getId());

        LOGGER.info("CreateBalanceCommand : output: ({})", ObjectLogger.log(output));

        return output;
    }

}

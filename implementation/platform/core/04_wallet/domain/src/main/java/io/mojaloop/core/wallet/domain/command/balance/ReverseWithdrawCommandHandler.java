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

package io.mojaloop.core.wallet.domain.command.balance;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceUpdateId;
import io.mojaloop.core.wallet.contract.command.balance.ReverseWithdrawCommand;
import io.mojaloop.core.wallet.contract.exception.balance.BalanceUpdateIdNotFoundException;
import io.mojaloop.core.wallet.contract.exception.balance.ReversalFailedInWalletException;
import io.mojaloop.core.wallet.domain.component.BalanceUpdater;
import io.mojaloop.core.wallet.domain.repository.BalanceUpdateRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ReverseWithdrawCommandHandler implements ReverseWithdrawCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ReverseWithdrawCommandHandler.class);

    private final BalanceUpdateRepository balanceUpdateRepository;

    private final BalanceUpdater balanceUpdater;

    public ReverseWithdrawCommandHandler(BalanceUpdateRepository balanceUpdateRepository,
                                         BalanceUpdater balanceUpdater) {

        assert balanceUpdateRepository != null;
        assert balanceUpdater != null;

        this.balanceUpdateRepository = balanceUpdateRepository;
        this.balanceUpdater = balanceUpdater;
    }

    @Override
    public Output execute(final Input input) throws ReversalFailedInWalletException {

        LOGGER.info("ReverseWithdrawCommand : input: ({})", ObjectLogger.log(input));

        this.balanceUpdateRepository
            .findById(input.withdrawId())
            .orElseThrow(() -> new BalanceUpdateIdNotFoundException(input.withdrawId()));

        var alreadyReversed = this.balanceUpdateRepository
                                  .findOne(BalanceUpdateRepository.Filters.withReversalId(
                                      input.withdrawId()))
                                  .isPresent();

        if (alreadyReversed) {
            throw new ReversalFailedInWalletException(input.withdrawId());
        }

        try {

            final var history = this.balanceUpdater.reverse(
                input.withdrawId(), new BalanceUpdateId(Snowflake.get().nextId()));

            final var output = new Output(
                history.balanceUpdateId(), history.balanceId(), history.action(),
                history.transactionId(), history.currency(), history.amount(), history.oldBalance(),
                history.newBalance(), history.transactionAt(), history.reversalId());

            LOGGER.info("ReverseWithdrawCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (BalanceUpdater.ReversalFailedException e) {
            throw new ReversalFailedInWalletException(e.getReversalId());
        }
    }

}

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

package org.mojave.accounting.domain.command.ledger;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.LedgerMovementId;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.accounting.contract.command.ledger.PostLedgerFlowCommand;
import org.mojave.accounting.contract.exception.account.AccountNotActiveException;
import org.mojave.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import org.mojave.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import org.mojave.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import org.mojave.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import org.mojave.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.accounting.contract.ledger.Ledger;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Objects;

@Service
public class PostLedgerFlowCommandHandler implements PostLedgerFlowCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        PostLedgerFlowCommandHandler.class);

    private final AccountCache accountCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final Ledger ledger;

    public PostLedgerFlowCommandHandler(AccountCache accountCache,
                                        FlowDefinitionCache flowDefinitionCache,
                                        Ledger ledger) {

        Objects.requireNonNull(accountCache);
        Objects.requireNonNull(flowDefinitionCache);
        Objects.requireNonNull(ledger);

        this.accountCache = accountCache;
        this.flowDefinitionCache = flowDefinitionCache;
        this.ledger = ledger;
    }

    @Override
    public Output execute(Input input) throws
                                       InsufficientBalanceInAccountException,
                                       OverdraftLimitReachedInAccountException,
                                       DuplicatePostingInLedgerException,
                                       PostingAccountNotFoundException,
                                       RestoreFailedInAccountException {

        LOGGER.info("PostLedgerFlowCommand : input: ({})", ObjectLogger.log(input));

        var transactionId = input.transactionId();
        var flowDefinition = this.flowDefinitionCache.get(
            input.transactionType(), input.currency());

        if (flowDefinition == null) {

            throw new FlowDefinitionNotConfiguredException(
                input.transactionType(), input.currency());
        }

        if (flowDefinition.activationStatus() != ActivationStatus.ACTIVE ||
                flowDefinition.terminationStatus() != TerminationStatus.ALIVE) {

            throw new FlowDefinitionNotConfiguredException(
                input.transactionType(), input.currency());
        }

        var requests = new ArrayList<Ledger.Request>();

        var lines = flowDefinition.flowLines();

        for (var flowLine : lines) {

            final var amount = input.amounts().get(flowLine.amountName());

            if (amount == null) {

                throw new RequiredAmountNameNotFoundInTransactionException(
                    flowLine.amountName(), input.amounts().keySet(), transactionId);
            }

            final var accountOfParticipant = input.participants().get(flowLine.participant());

            if (accountOfParticipant == null) {

                throw new RequiredParticipantNotFoundInTransactionException(
                    flowLine.participant(), input.participants().keySet(), transactionId);
            }

            final var coaEntryId = flowLine.coaEntryId();
            final var accountData = this.accountCache.get(
                coaEntryId, accountOfParticipant, input.currency());

            if (accountData == null) {
                throw new PostingAccountNotFoundException(
                    accountOfParticipant, coaEntryId, input.currency());
            }

            final AccountId accountId = accountData.accountId();

            if (accountData.activationStatus() != ActivationStatus.ACTIVE) {
                throw new AccountNotActiveException(accountData.code());
            }

            var request = new Ledger.Request(
                new LedgerMovementId(Snowflake.get().nextId()), flowLine.step(), accountId,
                flowLine.side(), input.currency(), amount, flowDefinition.flowDefinitionId(),
                flowLine.flowLineId());

            requests.add(request);
        }

        try {

            final var movements = new ArrayList<Output.Movement>();

            this.ledger
                .post(
                    requests, input.transactionId(), input.transactionAt(),
                    input.transactionType())
                .forEach(movement -> {

                    var accountData = this.accountCache.get(movement.accountId());

                    movements.add(new Output.Movement(
                        movement.ledgerMovementId(), movement.accountId(), accountData.ownerId(),
                        accountData.coaEntryId(), movement.side(), movement.currency(),
                        movement.amount(),
                        new Output.DrCr(movement.oldDrCr().debits(), movement.oldDrCr().credits()),
                        new Output.DrCr(movement.newDrCr().debits(), movement.newDrCr().credits()),
                        movement.movementStage(), movement.movementResult(), movement.createdAt()));
                });

            var output = new Output(
                input.transactionId(), input.transactionAt(), input.transactionType(),
                flowDefinition.flowDefinitionId(), movements);

            LOGGER.info("PostLedgerFlowCommand : output: ({})", ObjectLogger.log(output));

            return output;

        } catch (Ledger.InsufficientBalanceException e) {

            LOGGER.error("Error:", e);

            var accountData = this.accountCache.get(e.getAccountId());

            throw new InsufficientBalanceInAccountException(
                accountData.code(), e.getSide(),
                e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.OverdraftExceededException e) {

            LOGGER.error("Error:", e);

            var accountData = this.accountCache.get(e.getAccountId());

            throw new OverdraftLimitReachedInAccountException(
                accountData.code(), e.getSide(),
                e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.RestoreFailedException e) {

            LOGGER.error("Error:", e);

            var accountData = this.accountCache.get(e.getAccountId());

            throw new RestoreFailedInAccountException(
                accountData.code(), e.getSide(),
                e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.DuplicatePostingException e) {

            LOGGER.error("Error:", e);

            var accountData = this.accountCache.get(e.getAccountId());

            throw new DuplicatePostingInLedgerException(
                accountData.code(), e.getSide(), input.transactionId());
        }
    }

}

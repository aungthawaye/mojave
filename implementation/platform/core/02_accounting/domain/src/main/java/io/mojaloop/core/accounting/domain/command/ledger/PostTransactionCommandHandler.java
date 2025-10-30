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

package io.mojaloop.core.accounting.domain.command.ledger;

import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.command.ledger.PostTransactionCommand;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountNotActiveException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.component.ledger.Ledger;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.accounting.ReceiveIn;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.LedgerMovementId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.ArrayList;

@Service
public class PostTransactionCommandHandler implements PostTransactionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(PostTransactionCommandHandler.class);

    private final AccountCache accountCache;

    private final FlowDefinitionCache flowDefinitionCache;

    private final Ledger ledger;

    public PostTransactionCommandHandler(AccountCache accountCache, FlowDefinitionCache flowDefinitionCache, Ledger ledger) {

        assert accountCache != null;
        assert flowDefinitionCache != null;
        assert ledger != null;

        this.accountCache = accountCache;
        this.flowDefinitionCache = flowDefinitionCache;
        this.ledger = ledger;
    }

    @Override
    public Output execute(Input input)
        throws InsufficientBalanceInAccountException, OverdraftLimitReachedInAccountException, DuplicatePostingInLedgerException, RestoreFailedInAccountException {

        LOGGER.info("Executing PostTransactionCommand with input: {}", input);

        var transactionId = input.transactionId();
        var flowDefinition = this.flowDefinitionCache.get(input.transactionType(), input.currency());

        if (flowDefinition == null) {
            LOGGER.error("Flow Definition not found for transaction type: {} and currency: {}", input.transactionType(), input.currency());
            throw new FlowDefinitionNotConfiguredException(input.transactionType(), input.currency());
        }

        var requests = new ArrayList<Ledger.Request>();

        flowDefinition.postings().forEach(posting -> {

            var amount = input.amounts().get(posting.amountName());

            if (amount == null) {

                LOGGER.error("Required amount name not found in transaction: amount name : {}, amounts : {}, transactionId : {}",
                             posting.amountName(),
                             input.amounts().keySet(),
                             transactionId.getId().toString());

                throw new RequiredAmountNameNotFoundInTransactionException(posting.amountName(), input.amounts().keySet(), transactionId);
            }

            AccountId accountId = null;
            AccountData accountData = null;

            if (posting.receiveIn() == ReceiveIn.CHART_ENTRY) {

                var accountOfParticipant = input.participants().get(posting.participant());

                if (accountOfParticipant == null) {

                    LOGGER.error("Required participant ({}) not found in participants ({}) of Transaction Id ({})",
                                 posting.participant(),
                                 input.participants().keySet(),
                                 transactionId.getId().toString());

                    throw new RequiredParticipantNotFoundInTransactionException(posting.participant(), input.participants().keySet(), transactionId);
                }

                accountData = this.accountCache.get(new ChartEntryId(posting.receiveInId()), accountOfParticipant, input.currency());
                accountId = accountData.accountId();

            } else {

                accountId = new AccountId(posting.receiveInId());
                accountData = this.accountCache.get(accountId);

                if (accountData == null) {
                    LOGGER.error("Account not found for account id: {}", accountId);
                    throw new AccountIdNotFoundException(accountId);
                }

            }

            if (accountData.activationStatus() != ActivationStatus.ACTIVE) {
                LOGGER.warn("Account {} is not active", accountId);
                throw new AccountNotActiveException(accountData.code());
            }

            var request = new Ledger.Request(new LedgerMovementId(Snowflake.get().nextId()),
                                             accountId,
                                             posting.side(),
                                             input.currency(),
                                             amount,
                                             flowDefinition.flowDefinitionId(),
                                             posting.postingDefinitionId());

            requests.add(request);
        });

        try {

            var movements = new ArrayList<Output.Movement>();

            this.ledger.post(requests, input.transactionId(), input.transactionAt(), input.transactionType()).forEach(movement -> {

                var accountData = this.accountCache.get(movement.accountId());

                movements.add(new Output.Movement(movement.ledgerMovementId(),
                                                  movement.accountId(),
                                                  accountData.ownerId(),
                                                  accountData.chartEntryId(),
                                                  movement.side(),
                                                  movement.currency(),
                                                  movement.amount(),
                                                  new Output.DrCr(movement.oldDrCr().debits(), movement.oldDrCr().credits()),
                                                  new Output.DrCr(movement.oldDrCr().debits(), movement.oldDrCr().credits()),
                                                  movement.movementStage(),
                                                  movement.movementResult(),
                                                  movement.createdAt()));
            });

            return new Output(input.transactionId(), input.transactionAt(), input.transactionType(), flowDefinition.flowDefinitionId(), movements);

        } catch (Ledger.InsufficientBalanceException e) {

            var accountData = this.accountCache.get(e.getAccountId());
            LOGGER.error("Insufficient balance in account: code : {} | side : {} | amount : {} | debits : {} | credits : {} | transactionId : {}",
                         accountData.code(),
                         e.getSide(),
                         e.getAmount(),
                         e.getDrCr().debits(),
                         e.getDrCr().credits(),
                         input.transactionId());
            throw new InsufficientBalanceInAccountException(accountData.code(), e.getSide(), e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.OverdraftExceededException e) {

            var accountData = this.accountCache.get(e.getAccountId());
            LOGGER.error("Insufficient balance in account: code : {} | side : {} | amount : {} | debits : {} | credits : {} | transactionId : {}",
                         accountData.code(),
                         e.getSide(),
                         e.getAmount(),
                         e.getDrCr().debits(),
                         e.getDrCr().credits(),
                         input.transactionId());

            throw new OverdraftLimitReachedInAccountException(accountData.code(), e.getSide(), e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.RestoreFailedException e) {

            var accountData = this.accountCache.get(e.getAccountId());
            LOGGER.error("Restore failed in account: code : {} | side : {} | amount : {} | debits : {} | credits : {} | transactionId : {}",
                         accountData.code(),
                         e.getSide(),
                         e.getAmount(),
                         e.getDrCr().debits(),
                         e.getDrCr().credits(),
                         input.transactionId());

            throw new RestoreFailedInAccountException(accountData.code(), e.getSide(), e.getAmount(), e.getDrCr().debits(), e.getDrCr().credits(), input.transactionId());

        } catch (Ledger.DuplicatePostingException e) {

            var accountData = this.accountCache.get(e.getAccountId());
            LOGGER.error("Duplicate posting in ledger : code : {} | side : {} | transactionId : {}", accountData.code(), e.getSide(), input.transactionId());

            throw new DuplicatePostingInLedgerException(accountData.code(), e.getSide(), input.transactionId());
        }
    }

}

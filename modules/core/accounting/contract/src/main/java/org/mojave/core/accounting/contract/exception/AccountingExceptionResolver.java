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

package org.mojave.core.accounting.contract.exception;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import org.mojave.core.accounting.contract.exception.account.AccountCodeRequiredException;
import org.mojave.core.accounting.contract.exception.account.AccountDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.contract.exception.account.AccountNameRequiredException;
import org.mojave.core.accounting.contract.exception.account.AccountNameTooLongException;
import org.mojave.core.accounting.contract.exception.account.AccountNotActiveException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryCodeAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryNameAlreadyExistsException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryNameRequiredException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryNameTooLongException;
import org.mojave.core.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.core.accounting.contract.exception.chart.CoaNameRequiredException;
import org.mojave.core.accounting.contract.exception.chart.CoaNameTooLongException;
import org.mojave.core.accounting.contract.exception.definition.CoaEntryConflictInDefinitionException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import org.mojave.core.accounting.contract.exception.definition.DefinitionNameTooLongException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.ImmatureCoaEntryException;
import org.mojave.core.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.InvalidParticipantForTransactionTypeException;
import org.mojave.core.accounting.contract.exception.definition.FlowLineNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.RequireParticipantForCoaEntryException;
import org.mojave.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import org.mojave.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import org.mojave.core.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import org.mojave.core.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import org.mojave.core.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import org.mojave.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;

public class AccountingExceptionResolver {

    public static Throwable resolve(final RestErrorResponse error) {

        final var code = error.code();
        final var extra = error.extras();

        return switch (code) {

            // account
            case AccountCodeNotFoundException.CODE -> AccountCodeNotFoundException.from(extra);
            case AccountCodeRequiredException.CODE -> AccountCodeRequiredException.from(extra);
            case AccountDescriptionTooLongException.CODE ->
                AccountDescriptionTooLongException.from(extra);
            case AccountIdNotFoundException.CODE -> AccountIdNotFoundException.from(extra);
            case AccountNameRequiredException.CODE -> AccountNameRequiredException.from(extra);
            case AccountNameTooLongException.CODE -> AccountNameTooLongException.from(extra);
            case AccountNotActiveException.CODE -> AccountNotActiveException.from(extra);

            // chart
            case CoaEntryCodeAlreadyExistsException.CODE ->
                CoaEntryCodeAlreadyExistsException.from(extra);
            case CoaEntryDescriptionTooLongException.CODE ->
                CoaEntryDescriptionTooLongException.from(extra);
            case CoaEntryIdNotFoundException.CODE -> CoaEntryIdNotFoundException.from(extra);
            case CoaEntryNameAlreadyExistsException.CODE ->
                CoaEntryNameAlreadyExistsException.from(extra);
            case CoaEntryNameRequiredException.CODE ->
                CoaEntryNameRequiredException.from(extra);
            case CoaEntryNameTooLongException.CODE -> CoaEntryNameTooLongException.from(extra);
            case CoaIdNotFoundException.CODE -> CoaIdNotFoundException.from(extra);
            case CoaNameRequiredException.CODE -> CoaNameRequiredException.from(extra);
            case CoaNameTooLongException.CODE -> CoaNameTooLongException.from(extra);

            // definition
            case CoaEntryConflictInDefinitionException.CODE ->
                CoaEntryConflictInDefinitionException.from(extra);
            case DefinitionDescriptionTooLongException.CODE ->
                DefinitionDescriptionTooLongException.from(extra);
            case DefinitionNameTooLongException.CODE -> DefinitionNameTooLongException.from(extra);
            case FlowDefinitionNameTakenException.CODE ->
                FlowDefinitionNameTakenException.from(extra);
            case FlowDefinitionNotConfiguredException.CODE ->
                FlowDefinitionNotConfiguredException.from(extra);
            case FlowDefinitionNotFoundException.CODE ->
                FlowDefinitionNotFoundException.from(extra);
            case FlowDefinitionAlreadyConfiguredException.CODE ->
                FlowDefinitionAlreadyConfiguredException.from(extra);
            case ImmatureCoaEntryException.CODE -> ImmatureCoaEntryException.from(extra);
            case InvalidAmountNameForTransactionTypeException.CODE ->
                InvalidAmountNameForTransactionTypeException.from(extra);
            case InvalidParticipantForTransactionTypeException.CODE ->
                InvalidParticipantForTransactionTypeException.from(extra);
            case FlowLineNotFoundException.CODE ->
                FlowLineNotFoundException.from(extra);
            case RequireParticipantForCoaEntryException.CODE ->
                RequireParticipantForCoaEntryException.from(extra);

            // ledger
            case DuplicatePostingInLedgerException.CODE ->
                DuplicatePostingInLedgerException.from(extra);
            case InsufficientBalanceInAccountException.CODE ->
                InsufficientBalanceInAccountException.from(extra);
            case OverdraftLimitReachedInAccountException.CODE ->
                OverdraftLimitReachedInAccountException.from(extra);
            case PostingAccountNotFoundException.CODE ->
                PostingAccountNotFoundException.from(extra);
            case RequiredAmountNameNotFoundInTransactionException.CODE ->
                RequiredAmountNameNotFoundInTransactionException.from(extra);
            case RequiredParticipantNotFoundInTransactionException.CODE ->
                RequiredParticipantNotFoundInTransactionException.from(extra);
            case RestoreFailedInAccountException.CODE ->
                RestoreFailedInAccountException.from(extra);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}

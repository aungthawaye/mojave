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

package io.mojaloop.core.accounting.contract.exception;

import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountCodeRequiredException;
import io.mojaloop.core.accounting.contract.exception.account.AccountDescriptionTooLongException;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountNameRequiredException;
import io.mojaloop.core.accounting.contract.exception.account.AccountNameTooLongException;
import io.mojaloop.core.accounting.contract.exception.account.AccountNotActiveException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryCodeAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryDescriptionTooLongException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameRequiredException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameTooLongException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartNameRequiredException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartNameTooLongException;
import io.mojaloop.core.accounting.contract.exception.definition.AccountConflictInDefinitionException;
import io.mojaloop.core.accounting.contract.exception.definition.AmbiguousReceiveInConfigException;
import io.mojaloop.core.accounting.contract.exception.definition.ChartEntryConflictInDefinitionException;
import io.mojaloop.core.accounting.contract.exception.definition.DefinitionDescriptionTooLongException;
import io.mojaloop.core.accounting.contract.exception.definition.DefinitionNameTooLongException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNameTakenException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotConfiguredException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.exception.definition.FlowDefinitionAlreadyConfiguredException;
import io.mojaloop.core.accounting.contract.exception.definition.ImmatureChartEntryException;
import io.mojaloop.core.accounting.contract.exception.definition.InvalidAmountNameForTransactionTypeException;
import io.mojaloop.core.accounting.contract.exception.definition.InvalidParticipantForTransactionTypeException;
import io.mojaloop.core.accounting.contract.exception.definition.PostingDefinitionNotFoundException;
import io.mojaloop.core.accounting.contract.exception.definition.RequireParticipantForReceiveInException;
import io.mojaloop.core.accounting.contract.exception.ledger.DuplicatePostingInLedgerException;
import io.mojaloop.core.accounting.contract.exception.ledger.InsufficientBalanceInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.OverdraftLimitReachedInAccountException;
import io.mojaloop.core.accounting.contract.exception.ledger.PostingAccountNotFoundException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredAmountNameNotFoundInTransactionException;
import io.mojaloop.core.accounting.contract.exception.ledger.RequiredParticipantNotFoundInTransactionException;
import io.mojaloop.core.accounting.contract.exception.ledger.RestoreFailedInAccountException;

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
            case ChartEntryCodeAlreadyExistsException.CODE ->
                ChartEntryCodeAlreadyExistsException.from(extra);
            case ChartEntryDescriptionTooLongException.CODE ->
                ChartEntryDescriptionTooLongException.from(extra);
            case ChartEntryIdNotFoundException.CODE -> ChartEntryIdNotFoundException.from(extra);
            case ChartEntryNameAlreadyExistsException.CODE ->
                ChartEntryNameAlreadyExistsException.from(extra);
            case ChartEntryNameRequiredException.CODE ->
                ChartEntryNameRequiredException.from(extra);
            case ChartEntryNameTooLongException.CODE -> ChartEntryNameTooLongException.from(extra);
            case ChartIdNotFoundException.CODE -> ChartIdNotFoundException.from(extra);
            case ChartNameRequiredException.CODE -> ChartNameRequiredException.from(extra);
            case ChartNameTooLongException.CODE -> ChartNameTooLongException.from(extra);

            // definition
            case AccountConflictInDefinitionException.CODE ->
                AccountConflictInDefinitionException.from(extra);
            case AmbiguousReceiveInConfigException.CODE ->
                AmbiguousReceiveInConfigException.from(extra);
            case ChartEntryConflictInDefinitionException.CODE ->
                ChartEntryConflictInDefinitionException.from(extra);
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
            case ImmatureChartEntryException.CODE -> ImmatureChartEntryException.from(extra);
            case InvalidAmountNameForTransactionTypeException.CODE ->
                InvalidAmountNameForTransactionTypeException.from(extra);
            case InvalidParticipantForTransactionTypeException.CODE ->
                InvalidParticipantForTransactionTypeException.from(extra);
            case PostingDefinitionNotFoundException.CODE ->
                PostingDefinitionNotFoundException.from(extra);
            case RequireParticipantForReceiveInException.CODE ->
                RequireParticipantForReceiveInException.from(extra);

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

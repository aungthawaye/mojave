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
package org.mojave.core.wallet.contract.command.balance;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.enums.wallet.BalanceAction;
import org.mojave.core.common.datatype.identifier.transaction.TransactionId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.exception.balance.InsufficientBalanceException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.specification.fspiop.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

public interface WithdrawFundCommand {

    Output execute(Input input)
        throws NoBalanceUpdateForTransactionException, InsufficientBalanceException;

    record Input(@JsonProperty(required = true) @NotNull WalletOwnerId walletOwnerId,
                 @JsonProperty(required = true) @NotNull Currency currency,
                 @JsonProperty(required = true) @NotNull BigDecimal amount,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull Instant transactionAt,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    record Output(BalanceUpdateId balanceUpdateId,
                  BalanceId balanceId,
                  BalanceAction action,
                  TransactionId transactionId,
                  Currency currency,
                  BigDecimal amount,
                  BigDecimal oldBalance,
                  BigDecimal newBalance,
                  Instant transactionAt) { }

}

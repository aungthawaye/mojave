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

package org.mojave.core.wallet.contract.command.ndc;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.wallet.contract.exception.WalletNotFoundException;
import org.mojave.core.wallet.contract.exception.balance.NoBalanceUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.ndc.BalanceLowerThanNewNdcException;
import org.mojave.scheme.rule.identifier.transaction.TransactionId;
import org.mojave.scheme.rule.identifier.wallet.NdcUpdateId;
import org.mojave.scheme.rule.identifier.wallet.WalletId;

import java.math.BigDecimal;
import java.time.Instant;

public interface IncreaseNdcCommand {

    String SUBJECT_NAME = "sub-wallet.increase-ndc-command";

    String TOPIC_NAME = "tp-wallet.increase-ndc-command";

    Output execute(Input input) throws
                                NoBalanceUpdateForTransactionException,
                                BalanceLowerThanNewNdcException,
                                WalletNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull WalletId walletId,
                 @JsonProperty(required = true) @NotNull BigDecimal amount,
                 @JsonProperty(required = true) @NotNull TransactionId transactionId,
                 @JsonProperty(required = true) @NotNull Instant transactionAt,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description) { }

    record Output(NdcUpdateId ndcUpdateId) { }

}

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

package org.mojave.core.wallet.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.WalletData;

import java.util.List;

public interface WalletQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-wallet.wallet-query.get-by-id";

    String GET_BY_OWNER_ID_CURRENCY_TAG_SUBJECT_NAME =
        "sub-wallet.wallet-query.get-by-owner-id-currency-tag";

    String GET_BY_OWNER_ID_SUBJECT_NAME = "sub-wallet.wallet-query.get-by-owner-id";

    String GET_BY_OWNER_ID_CURRENCY_SUBJECT_NAME =
        "sub-wallet.wallet-query.get-by-owner-id-currency";

    String GET_ALL_SUBJECT_NAME = "sub-wallet.wallet-query.get-all";

    WalletData get(WalletId walletId);

    WalletData get(WalletOwnerId ownerId, Currency currency, String tag);

    List<WalletData> get(WalletOwnerId ownerId);

    List<WalletData> get(WalletOwnerId ownerId, Currency currency);

    List<WalletData> getAll();

    record GetByIdInput(@JsonProperty(required = true) @NotNull WalletId walletId) { }

    record GetByOwnerIdCurrencyTagInput(
        @JsonProperty(required = true) @NotNull WalletOwnerId ownerId,
        @JsonProperty(required = true) @NotNull Currency currency,
        @JsonProperty(required = true) @NotNull String tag) { }

    record GetByOwnerIdInput(@JsonProperty(required = true) @NotNull WalletOwnerId ownerId) { }

    record GetByOwnerIdCurrencyInput(
        @JsonProperty(required = true) @NotNull WalletOwnerId ownerId,
        @JsonProperty(required = true) @NotNull Currency currency) { }

    record GetAllInput() { }

}

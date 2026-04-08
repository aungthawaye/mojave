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

package org.mojave.wallet.domain.query;

import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.wallet.WalletId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.wallet.contract.data.WalletData;
import org.mojave.wallet.contract.exception.position.PositionIdNotFoundException;
import org.mojave.wallet.contract.query.PositionQuery;
import org.mojave.wallet.domain.model.Wallet;
import org.mojave.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PositionQueryHandler implements PositionQuery {

    private final WalletRepository walletRepository;

    public PositionQueryHandler(final WalletRepository walletRepository) {

        Objects.requireNonNull(walletRepository);

        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public WalletData get(final WalletId walletId) {

        return this.walletRepository
                   .findById(walletId)
                   .orElseThrow(() -> new PositionIdNotFoundException(walletId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<WalletData> get(final WalletOwnerId ownerId, final Currency currency) {

        final var spec = WalletRepository.Filters
                             .withOwnerId(ownerId)
                             .and(WalletRepository.Filters.withCurrency(currency))
                             .and(WalletRepository.Filters.withScenario(Wallet.DEFAULT_SCENARIO));

        return this.walletRepository.findAll(spec).stream().map(Wallet::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<WalletData> getAll() {

        final var spec = WalletRepository.Filters.withScenario(Wallet.DEFAULT_SCENARIO);

        return this.walletRepository.findAll(spec).stream().map(Wallet::convert).toList();
    }

}

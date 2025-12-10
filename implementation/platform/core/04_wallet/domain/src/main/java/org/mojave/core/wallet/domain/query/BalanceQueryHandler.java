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

package org.mojave.core.wallet.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.common.datatype.identifier.wallet.BalanceId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.BalanceData;
import org.mojave.core.wallet.contract.exception.balance.BalanceIdNotFoundException;
import org.mojave.core.wallet.contract.query.BalanceQuery;
import org.mojave.core.wallet.domain.model.Balance;
import org.mojave.core.wallet.domain.repository.BalanceRepository;
import org.mojave.fspiop.spec.core.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class BalanceQueryHandler implements BalanceQuery {

    private final BalanceRepository balanceRepository;

    public BalanceQueryHandler(final BalanceRepository balanceRepository) {

        assert balanceRepository != null;

        this.balanceRepository = balanceRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public BalanceData get(final BalanceId balanceId) {

        return this.balanceRepository
                   .findById(balanceId)
                   .orElseThrow(() -> new BalanceIdNotFoundException(balanceId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<BalanceData> get(final WalletOwnerId ownerId, final Currency currency) {

        var spec = BalanceRepository.Filters
                       .withOwnerId(ownerId)
                       .and(BalanceRepository.Filters.withCurrency(currency));

        return this.balanceRepository.findAll(spec).stream().map(Balance::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<BalanceData> getAll() {

        return this.balanceRepository.findAll().stream().map(Balance::convert).toList();
    }

}

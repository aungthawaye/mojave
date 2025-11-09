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
package io.mojaloop.core.wallet.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.exception.wallet.WalletIdNotFoundException;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
import io.mojaloop.core.wallet.domain.model.Wallet;
import io.mojaloop.core.wallet.domain.repository.WalletRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
public class WalletQueryHandler implements WalletQuery {

    private final WalletRepository walletRepository;

    public WalletQueryHandler(final WalletRepository walletRepository) {

        assert walletRepository != null;

        this.walletRepository = walletRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public WalletData get(final WalletId walletId) {

        return this.walletRepository.findById(walletId).orElseThrow(() -> new WalletIdNotFoundException(walletId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<WalletData> getAll() {

        return this.walletRepository.findAll().stream().map(Wallet::convert).toList();
    }

}

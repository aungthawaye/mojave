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
package org.mojave.core.wallet.domain.repository;

import org.mojave.core.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.core.wallet.domain.model.BalanceUpdate;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface BalanceUpdateRepository
    extends JpaRepository<BalanceUpdate, BalanceUpdateId>, JpaSpecificationExecutor<BalanceUpdate> {

    class Filters {

        public static Specification<BalanceUpdate> withReversalId(final BalanceUpdateId walletId) {

            return (root, query, cb) -> cb.equal(root.get("withdrawId"), walletId);
        }

        public static Specification<BalanceUpdate> withWalletId(final BalanceUpdateId walletId) {

            return (root, query, cb) -> cb.equal(root.get("balanceId"), walletId);
        }

    }

}

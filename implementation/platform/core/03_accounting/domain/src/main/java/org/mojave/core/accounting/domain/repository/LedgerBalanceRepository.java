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

package org.mojave.core.accounting.domain.repository;

import org.mojave.core.accounting.domain.model.ledger.LedgerBalance;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.LedgerBalanceId;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface LedgerBalanceRepository
    extends JpaRepository<LedgerBalance, LedgerBalanceId>, JpaSpecificationExecutor<LedgerBalance> {

    class Filters {

        public static Specification<LedgerBalance> withAccountId(AccountId accountId) {

            return (root, query, cb) -> cb.equal(root.get("account").get("id"), accountId);
        }

        public static Specification<LedgerBalance> withId(LedgerBalanceId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

    }

}

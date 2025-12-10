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

import org.mojave.core.accounting.domain.model.Account;
import org.mojave.core.common.datatype.identifier.accounting.AccountId;
import org.mojave.core.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.core.common.datatype.identifier.accounting.ChartEntryId;
import org.mojave.core.common.datatype.type.accounting.AccountCode;
import org.mojave.fspiop.spec.core.Currency;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface AccountRepository
    extends JpaRepository<Account, AccountId>, JpaSpecificationExecutor<Account> {

    class Filters {

        public static Specification<Account> withChartEntryId(ChartEntryId chartEntryId) {

            return (root, query, cb) -> cb.equal(root.get("chartEntryId"), chartEntryId);
        }

        public static Specification<Account> withCode(AccountCode code) {

            return (root, query, cb) -> cb.equal(root.get("code"), code);
        }

        public static Specification<Account> withCurrency(Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<Account> withId(AccountId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Account> withOwnerId(AccountOwnerId ownerId) {

            return (root, query, cb) -> cb.equal(root.get("ownerId"), ownerId);
        }

    }

}

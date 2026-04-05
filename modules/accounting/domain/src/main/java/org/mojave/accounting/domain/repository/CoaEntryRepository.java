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

package org.mojave.accounting.domain.repository;

import org.mojave.accounting.domain.model.CoaEntry;
import org.mojave.accounting.domain.model.CoaEntry_;
import org.mojave.accounting.domain.model.Coa_;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface CoaEntryRepository
    extends JpaRepository<CoaEntry, CoaEntryId>, JpaSpecificationExecutor<CoaEntry> {

    class Filters {

        public static Specification<CoaEntry> withAccountType(AccountType type) {

            return (root, query, cb) -> cb.equal(root.get(CoaEntry_.accountType), type);
        }

        public static Specification<CoaEntry> withCategory(String category) {

            return (root, query, cb) -> cb.equal(root.get(CoaEntry_.category), category);
        }

        public static Specification<CoaEntry> withCoaId(CoaId coaId) {

            return (root, query, cb) -> cb.equal(root.get(CoaEntry_.coa).get(Coa_.id), coaId);
        }

        public static Specification<CoaEntry> withCode(CoaEntryCode code) {

            return (root, query, cb) -> cb.equal(root.get(CoaEntry_.code), code);
        }

        public static Specification<CoaEntry> withId(CoaEntryId id) {

            return (root, query, cb) -> cb.equal(root.get(CoaEntry_.id), id);
        }

        public static Specification<CoaEntry> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get(CoaEntry_.name), "%" + name + "%");
        }

    }

}

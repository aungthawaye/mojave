/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.accounting.domain.repository;

import io.mojaloop.core.accounting.domain.model.ChartEntry;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface ChartEntryRepository extends JpaRepository<ChartEntry, ChartEntryId>, JpaSpecificationExecutor<ChartEntry> {

    class Filters {

        public static Specification<ChartEntry> withAccountType(AccountType type) {

            return (root, query, cb) -> cb.equal(root.get("accountType"), type);
        }

        public static Specification<ChartEntry> withChartId(ChartId chartId) {

            return (root, query, cb) -> cb.equal(root.get("chart").get("id"), chartId);
        }

        public static Specification<ChartEntry> withCode(ChartEntryCode code) {

            return (root, query, cb) -> cb.equal(root.get("code"), code);
        }

        public static Specification<ChartEntry> withId(ChartEntryId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<ChartEntry> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }

    }

}

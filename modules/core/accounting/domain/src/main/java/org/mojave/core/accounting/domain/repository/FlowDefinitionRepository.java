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

package org.mojave.core.accounting.domain.repository;

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.enums.trasaction.TransactionType;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.core.accounting.domain.model.FlowDefinition;
import org.mojave.core.accounting.domain.model.FlowDefinition_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, FlowDefinitionId>,
                                                  JpaSpecificationExecutor<FlowDefinition> {

    class Filters {

        public static Specification<FlowDefinition> withActivationStatus(ActivationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(FlowDefinition_.activationStatus), status);
        }

        public static Specification<FlowDefinition> withCurrency(Currency currency) {

            return (root, query, cb) -> cb.equal(root.get(FlowDefinition_.currency), currency);
        }

        public static Specification<FlowDefinition> withId(FlowDefinitionId id) {

            return (root, query, cb) -> cb.equal(root.get(FlowDefinition_.id), id);
        }

        public static Specification<FlowDefinition> withIdNotEquals(FlowDefinitionId id) {

            return (root, query, cb) -> cb.notEqual(root.get(FlowDefinition_.id), id);
        }

        public static Specification<FlowDefinition> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get(FlowDefinition_.name), "%" + name + "%");
        }

        public static Specification<FlowDefinition> withNameEquals(String name) {

            return (root, query, cb) -> cb.equal(root.get(FlowDefinition_.name), name);
        }

        public static Specification<FlowDefinition> withTerminationStatus(TerminationStatus status) {

            return (root, query, cb) -> cb.equal(
                root.get(FlowDefinition_.terminationStatus), status);
        }

        public static Specification<FlowDefinition> withTransactionType(TransactionType type) {

            return (root, query, cb) -> cb.equal(root.get(FlowDefinition_.transactionType), type);
        }

    }

}

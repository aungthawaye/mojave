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
/*-
 * =============================================================================
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
 * =============================================================================
 */

package io.mojaloop.core.accounting.domain.repository;

import io.mojaloop.core.accounting.domain.model.FlowDefinition;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.TerminationStatus;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FlowDefinitionRepository extends JpaRepository<FlowDefinition, FlowDefinitionId>, JpaSpecificationExecutor<FlowDefinition> {

    class Filters {

        public static Specification<FlowDefinition> withActivationStatus(ActivationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("activationStatus"), status);
        }

        public static Specification<FlowDefinition> withCurrency(Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<FlowDefinition> withId(FlowDefinitionId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<FlowDefinition> withIdNotEquals(FlowDefinitionId id) {

            return (root, query, cb) -> cb.notEqual(root.get("id"), id);
        }

        public static Specification<FlowDefinition> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }

        public static Specification<FlowDefinition> withNameEquals(String name) {

            return (root, query, cb) -> cb.equal(root.get("name"), name);
        }

        public static Specification<FlowDefinition> withTerminationStatus(TerminationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("terminationStatus"), status);
        }

    }

}

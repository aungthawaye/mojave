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
package org.mojave.core.participant.domain.repository;

import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.OracleId;
import org.mojave.core.participant.domain.model.oracle.Oracle;
import org.mojave.fspiop.spec.core.PartyIdType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;

public interface OracleRepository
    extends JpaRepository<Oracle, OracleId>, JpaSpecificationExecutor<Oracle> {

    class Filters {

        public static Specification<Oracle> withId(OracleId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Oracle> withType(PartyIdType type) {

            return (root, query, cb) -> cb.equal(root.get("type"), type);
        }

        public static Specification<Oracle> withActivationStatus(ActivationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("activationStatus"), status);
        }

        public static Specification<Oracle> withTerminationStatus(TerminationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("terminationStatus"), status);
        }

        public static Specification<Oracle> withNameEquals(String name) {

            return (root, query, cb) -> cb.equal(root.get("name"), name);
        }

    }

}

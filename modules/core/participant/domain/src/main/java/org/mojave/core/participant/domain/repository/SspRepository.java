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

import org.mojave.common.datatype.enums.ActivationStatus;
import org.mojave.common.datatype.enums.TerminationStatus;
import org.mojave.common.datatype.identifier.participant.SspId;
import org.mojave.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.domain.model.ssp.Ssp;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface SspRepository extends JpaRepository<Ssp, SspId>, JpaSpecificationExecutor<Ssp> {

    class Filters {

        public static Specification<Ssp> withActivationStatus(final ActivationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("activationStatus"), status);
        }

        public static Specification<Ssp> withSspCode(final SspCode sspCode) {

            return (root, query, cb) -> cb.equal(root.get("code"), sspCode);
        }

        public static Specification<Ssp> withId(final SspId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Ssp> withNameContains(final String name) {

            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }

        public static Specification<Ssp> withNameEquals(final String name) {

            return (root, query, cb) -> cb.equal(root.get("name"), name);
        }

        public static Specification<Ssp> withTerminationStatus(final TerminationStatus status) {

            return (root, query, cb) -> cb.equal(root.get("terminationStatus"), status);
        }

    }

}

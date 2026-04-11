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
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.common.datatype.type.participant.FspCode;
import org.mojave.core.participant.domain.model.fsp.Fsp;
import org.mojave.core.participant.domain.model.fsp.Fsp_;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface FspRepository extends JpaRepository<Fsp, FspId>, JpaSpecificationExecutor<Fsp> {

    class Filters {

        public static Specification<Fsp> withActivationStatus(ActivationStatus status) {

            return (root, query, cb) -> cb.equal(root.get(Fsp_.activationStatus), status);
        }

        public static Specification<Fsp> withFspCode(FspCode fspCode) {

            return (root, query, cb) -> cb.equal(root.get(Fsp_.code), fspCode);
        }

        public static Specification<Fsp> withId(FspId id) {

            return (root, query, cb) -> cb.equal(root.get(Fsp_.id), id);
        }

        public static Specification<Fsp> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get(Fsp_.name), "%" + name + "%");
        }

        public static Specification<Fsp> withNameEquals(String name) {

            return (root, query, cb) -> cb.equal(root.get(Fsp_.name), name);
        }

        public static Specification<Fsp> withTerminationStatus(TerminationStatus status) {

            return (root, query, cb) -> cb.equal(root.get(Fsp_.terminationStatus), status);
        }

    }

}

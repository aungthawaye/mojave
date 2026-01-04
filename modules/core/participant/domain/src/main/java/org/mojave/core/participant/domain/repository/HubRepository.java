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

import org.mojave.scheme.common.datatype.identifier.participant.HubId;
import org.mojave.core.participant.domain.model.hub.Hub;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

@Repository
public interface HubRepository extends JpaRepository<Hub, HubId>, JpaSpecificationExecutor<Hub> {

    class Filters {

        public static Specification<Hub> withNameContains(String name) {

            return (root, query, cb) -> cb.like(root.get("name"), "%" + name + "%");
        }

        public static Specification<Hub> withNameEquals(String name) {

            return (root, query, cb) -> cb.equal(root.get("name"), name);
        }

    }

}

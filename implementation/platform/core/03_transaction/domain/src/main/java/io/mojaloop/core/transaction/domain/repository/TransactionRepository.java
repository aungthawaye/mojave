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

package io.mojaloop.core.transaction.domain.repository;

import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.domain.model.Transaction;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.stereotype.Repository;

import java.time.Instant;

@Repository
public interface TransactionRepository
    extends JpaRepository<Transaction, TransactionId>, JpaSpecificationExecutor<Transaction> {

    class Filters {

        public Specification<Transaction> completedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("completedAt"), from, to);
        }

        public Specification<Transaction> initiatedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("initiatedAt"), from, to);
        }

        public Specification<Transaction> reservedDuring(Instant from, Instant to) {

            return (root, query, cb) -> cb.between(root.get("reservedAt"), from, to);
        }

        public Specification<Transaction> withId(TransactionId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public Specification<Transaction> withStage(TransactionPhase stage) {

            return (root, query, cb) -> cb.equal(root.get("stage"), stage);
        }

    }

}

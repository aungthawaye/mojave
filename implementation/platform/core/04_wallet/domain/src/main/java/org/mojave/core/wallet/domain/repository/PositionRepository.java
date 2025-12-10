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

package org.mojave.core.wallet.domain.repository;

import org.mojave.core.common.datatype.identifier.wallet.PositionId;
import org.mojave.core.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.domain.model.Position;
import org.mojave.fspiop.spec.core.Currency;
import jakarta.persistence.LockModeType;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface PositionRepository
    extends JpaRepository<Position, PositionId>, JpaSpecificationExecutor<Position> {

    @Query("SELECT p FROM Position p WHERE p.id = :positionId")
    @Lock(LockModeType.PESSIMISTIC_WRITE)
    Optional<Position> selectForUpdate(PositionId positionId);

    class Filters {

        public static Specification<Position> withCurrency(final Currency currency) {

            return (root, query, cb) -> cb.equal(root.get("currency"), currency);
        }

        public static Specification<Position> withId(final PositionId id) {

            return (root, query, cb) -> cb.equal(root.get("id"), id);
        }

        public static Specification<Position> withOwnerId(final WalletOwnerId ownerId) {

            return (root, query, cb) -> cb.equal(root.get("walletOwnerId"), ownerId);
        }

    }

}

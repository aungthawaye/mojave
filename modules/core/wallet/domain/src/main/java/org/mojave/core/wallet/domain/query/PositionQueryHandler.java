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
package org.mojave.core.wallet.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.common.datatype.identifier.wallet.PositionId;
import org.mojave.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.core.wallet.contract.exception.position.PositionIdNotFoundException;
import org.mojave.core.wallet.contract.query.PositionQuery;
import org.mojave.core.wallet.domain.model.Position;
import org.mojave.core.wallet.domain.repository.PositionRepository;
import org.mojave.common.datatype.enums.Currency;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class PositionQueryHandler implements PositionQuery {

    private final PositionRepository positionRepository;

    public PositionQueryHandler(final PositionRepository positionRepository) {

        Objects.requireNonNull(positionRepository);

        this.positionRepository = positionRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public PositionData get(final PositionId positionId) {

        return this.positionRepository
                   .findById(positionId)
                   .orElseThrow(() -> new PositionIdNotFoundException(positionId))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<PositionData> get(final WalletOwnerId ownerId, final Currency currency) {

        var spec = PositionRepository.Filters
                       .withOwnerId(ownerId)
                       .and(PositionRepository.Filters.withCurrency(currency));

        return this.positionRepository.findAll(spec).stream().map(Position::convert).toList();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<PositionData> getAll() {

        return this.positionRepository.findAll().stream().map(Position::convert).toList();
    }

}

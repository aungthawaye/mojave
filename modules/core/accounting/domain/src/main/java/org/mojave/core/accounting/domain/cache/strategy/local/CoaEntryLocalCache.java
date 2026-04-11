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

package org.mojave.core.accounting.domain.cache.strategy.local;

import jakarta.annotation.PostConstruct;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.domain.cache.CoaEntryCache;
import org.mojave.core.accounting.domain.repository.CoaEntryRepository;

import java.util.HashSet;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

public class CoaEntryLocalCache implements CoaEntryCache {

    private final CoaEntryRepository coaEntryRepository;

    private final Map<Long, CoaEntryData> withId;

    private final Map<String, CoaEntryData> withCode;

    public CoaEntryLocalCache(final CoaEntryRepository coaEntryRepository) {

        Objects.requireNonNull(coaEntryRepository);

        this.coaEntryRepository = coaEntryRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withCode = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withCode.clear();
    }

    @Override
    public void delete(final CoaEntryId coaEntryId) {

        final var removed = this.withId.remove(coaEntryId.getId());

        if (removed != null) {
            this.withCode.remove(removed.code().value());
        }
    }

    @Override
    public CoaEntryData get(final CoaEntryId coaEntryId) {

        if (coaEntryId == null) {
            return null;
        }

        var data = this.withId.get(coaEntryId.getId());

        if (data == null) {

            var entity = this.coaEntryRepository.findById(coaEntryId).orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public CoaEntryData get(final CoaEntryCode code) {

        if (code == null) {
            return null;
        }

        var data = this.withCode.get(code.value());

        if (data == null) {

            var entity = this.coaEntryRepository
                             .findOne(CoaEntryRepository.Filters.withCode(code))
                             .orElse(null);

            if (entity != null) {
                data = entity.convert();
                this.save(data);
            }

            return data;
        }

        return data;
    }

    @Override
    public Set<CoaEntryData> get(final CoaId coaId) {

        if (coaId == null) {
            return Set.of();
        }

        final var result = new HashSet<CoaEntryData>();

        for (final var entry : this.withId.values()) {
            if (entry.coaId().equals(coaId)) {
                result.add(entry);
            }
        }

        if (result.isEmpty()) {
            var entities = this.coaEntryRepository.findAll(
                CoaEntryRepository.Filters.withCoaId(coaId));

            entities.forEach(entity -> {
                var converted = entity.convert();
                this.save(converted);
                if (converted.coaId().equals(coaId)) {
                    result.add(converted);
                }
            });
        }

        return result;
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var entries = this.coaEntryRepository.findAll();

        for (final var entry : entries) {
            this.save(entry.convert());
        }
    }

    @Override
    public void save(final CoaEntryData coaEntry) {

        this.withId.put(coaEntry.coaEntryId().getId(), coaEntry);
        this.withCode.put(coaEntry.code().value(), coaEntry);
    }

}

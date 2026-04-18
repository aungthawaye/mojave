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

package org.mojave.core.accounting.domain.model;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.scheme.rule.converter.identifier.accounting.CoaIdJavaType;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.data.CoaEntryData;
import org.mojave.core.accounting.contract.exception.chart.CoaNameRequiredException;
import org.mojave.core.accounting.contract.exception.chart.CoaNameTooLongException;

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "acc_coa",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "acc_coa_01_UK",
            columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Coa extends JpaEntity<CoaId> implements DataConversion<CoaData> {

    @Id
    @JavaType(CoaIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "coa_id",
        nullable = false,
        updatable = false)
    protected CoaId id;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "created_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @OneToMany(
        mappedBy = "coa",
        orphanRemoval = true,
        cascade = CascadeType.ALL,
        fetch = FetchType.EAGER)
    protected Set<CoaEntry> entries = new HashSet<>();

    public Coa(String name) {

        this.id = new CoaId(Snowflake.get().nextId());
        this.name(name);
        this.createdAt = Instant.now();
    }

    public CoaEntry addEntry(String category,
                             CoaEntryCode code,
                             String name,
                             String description,
                             AccountType accountType) {

        final var entry = new CoaEntry(this, category, code, name, description, accountType);

        this.entries.add(entry);

        return entry;
    }

    @Override
    public CoaData convert() {

        var entriesData = this
                              .getEntries()
                              .stream()
                              .map(CoaEntry::convert)
                              .toArray(CoaEntryData[]::new);

        return new CoaData(this.getId(), this.getName(), this.createdAt, entriesData);
    }

    public Set<CoaEntry> getEntries() {

        return Collections.unmodifiableSet(entries);
    }

    @Override
    public CoaId getId() {

        return this.id;
    }

    public Coa name(String name) {

        if (name == null || name.isBlank()) {
            throw new CoaNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new CoaNameTooLongException();
        }

        this.name = value;

        return this;
    }

    public boolean removeEntry(CoaEntryId coaEntryId) {

        return this.entries.removeIf(entry -> entry.getId().equals(coaEntryId));
    }

}

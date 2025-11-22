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

package io.mojaloop.core.accounting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.accounting.contract.data.ChartData;
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartNameRequiredException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartNameTooLongException;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.ChartIdJavaType;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
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

import java.time.Instant;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "acc_chart",
       uniqueConstraints = {@UniqueConstraint(name = "acc_chart_name_UK", columnNames = {"name"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Chart extends JpaEntity<ChartId> implements DataConversion<ChartData> {

    @Id
    @JavaType(ChartIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "chart_id", nullable = false, updatable = false)
    protected ChartId id;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "created_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @OneToMany(mappedBy = "chart",
               orphanRemoval = true,
               cascade = CascadeType.ALL,
               fetch = FetchType.EAGER)
    protected Set<ChartEntry> entries = new HashSet<>();

    public Chart(String name) {

        this.id = new ChartId(Snowflake.get().nextId());
        this.name(name);
        this.createdAt = Instant.now();
    }

    public ChartEntry addEntry(ChartEntryCode code,
                               String name,
                               String description,
                               AccountType accountType) {

        ChartEntry entry = new ChartEntry(this, code, name, description, accountType);

        this.entries.add(entry);

        return entry;
    }

    @Override
    public ChartData convert() {

        var entriesData = this
                              .getEntries()
                              .stream()
                              .map(ChartEntry::convert)
                              .toArray(ChartEntryData[]::new);

        return new ChartData(this.getId(), this.getName(), this.createdAt, entriesData);
    }

    public Set<ChartEntry> getEntries() {

        return Collections.unmodifiableSet(entries);
    }

    @Override
    public ChartId getId() {

        return this.id;
    }

    public Chart name(String name) {

        if (name == null || name.isBlank()) {
            throw new ChartNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new ChartNameTooLongException();
        }

        this.name = value;

        return this;
    }

    public boolean removeEntry(ChartEntryId chartEntryId) {

        return this.entries.removeIf(entry -> entry.getId().equals(chartEntryId));
    }

}

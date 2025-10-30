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
import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryCodeAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryDescriptionTooLongException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameAlreadyExistsException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameRequiredException;
import io.mojaloop.core.accounting.contract.exception.chart.ChartEntryNameTooLongException;
import io.mojaloop.core.accounting.domain.cache.redis.updater.ChartEntryCacheUpdater;
import io.mojaloop.core.common.datatype.converter.identifier.accounting.ChartEntryIdJavaType;
import io.mojaloop.core.common.datatype.converter.type.accounting.ChartEntryCodeConverter;
import io.mojaloop.core.common.datatype.enums.accounting.AccountType;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@EntityListeners(value = {ChartEntryCacheUpdater.class})
@Table(name = "acc_chart_entry", uniqueConstraints = @UniqueConstraint(name = "acc_chart_entry_chart_entry_code_UK", columnNames = {"chart_entry_code"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ChartEntry extends JpaEntity<ChartEntryId> implements DataConversion<ChartEntryData> {

    @Id
    @JavaType(ChartEntryIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "chart_entry_id", nullable = false, updatable = false)
    protected ChartEntryId id;

    @Column(name = "chart_entry_code", nullable = false, length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = ChartEntryCodeConverter.class)
    protected ChartEntryCode code;

    @Column(name = "name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(name = "description", nullable = false, length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "account_type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH, updatable = false)
    @Enumerated(EnumType.STRING)
    protected AccountType accountType;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "chart_id", nullable = false, updatable = false, foreignKey = @ForeignKey(name = "chart_entry_chart_FK"))
    protected Chart chart;

    public ChartEntry(Chart chart, ChartEntryCode code, String name, String description, AccountType accountType) {

        assert chart != null;
        assert code != null;
        assert name != null;
        assert description != null;
        assert accountType != null;

        this.id = new ChartEntryId(Snowflake.get().nextId());
        this.chart = chart;
        this.code(code).name(name).description(description);
        this.accountType = accountType;
        this.createdAt = Instant.now();
    }

    public ChartEntry code(ChartEntryCode code) {

        assert code != null;

        this.chart.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getCode().equals(code)) {
                throw new ChartEntryCodeAlreadyExistsException(code);
            }
        });

        this.chart.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getName().equalsIgnoreCase(name)) {
                throw new ChartEntryNameAlreadyExistsException(name, this.chart.name);
            }
        });

        this.code = code;

        return this;
    }

    @Override
    public ChartEntryData convert() {

        return new ChartEntryData(this.getId(), this.code, this.name, this.description, this.accountType, this.createdAt, this.chart.getId());
    }

    public ChartEntry description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new ChartEntryDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    @Override
    public ChartEntryId getId() {

        return this.id;
    }

    public ChartEntry name(String name) {

        if (name == null || name.isBlank()) {
            throw new ChartEntryNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new ChartEntryNameTooLongException();
        }

        this.name = value;

        return this;
    }

}

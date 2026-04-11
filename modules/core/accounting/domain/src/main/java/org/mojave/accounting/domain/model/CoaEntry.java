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

package org.mojave.accounting.domain.model;

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
import org.mojave.common.datatype.converter.identifier.accounting.CoaEntryIdJavaType;
import org.mojave.common.datatype.converter.type.accounting.CoaEntryCodeConverter;
import org.mojave.common.datatype.enums.accounting.AccountType;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.accounting.contract.data.CoaEntryData;
import org.mojave.accounting.contract.exception.chart.CoaEntryCodeAlreadyExistsException;
import org.mojave.accounting.contract.exception.chart.CoaEntryDescriptionTooLongException;
import org.mojave.accounting.contract.exception.chart.CoaEntryNameAlreadyExistsException;
import org.mojave.accounting.contract.exception.chart.CoaEntryNameRequiredException;
import org.mojave.accounting.contract.exception.chart.CoaEntryNameTooLongException;
import org.mojave.accounting.domain.cache.updater.CoaEntryCacheUpdater;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@EntityListeners(value = {CoaEntryCacheUpdater.class})
@Table(
    name = "acc_coa_entry",
    uniqueConstraints = @UniqueConstraint(
        name = "acc_coa_entry_01_UK",
        columnNames = {"coa_entry_code"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class CoaEntry extends JpaEntity<CoaEntryId> implements DataConversion<CoaEntryData> {

    @Id
    @JavaType(CoaEntryIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "coa_entry_id",
        nullable = false,
        updatable = false)
    protected CoaEntryId id;

    @Column(
        name = "category",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    protected String category;

    @Column(
        name = "coa_entry_code",
        nullable = false,
        length = StringSizeConstraints.MAX_CODE_LENGTH)
    @Convert(converter = CoaEntryCodeConverter.class)
    protected CoaEntryCode code;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "description",
        nullable = false,
        length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(
        name = "account_type",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH,
        updatable = false)
    @Enumerated(EnumType.STRING)
    protected AccountType accountType;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne
    @JoinColumn(
        name = "coa_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "acc_coa_acc_coa_entry_FK"))
    protected Coa coa;

    public CoaEntry(Coa coa,
                    String category,
                    CoaEntryCode code,
                    String name,
                    String description,
                    AccountType accountType) {

        Objects.requireNonNull(coa);
        Objects.requireNonNull(category);
        Objects.requireNonNull(code);
        Objects.requireNonNull(name);
        Objects.requireNonNull(description);
        Objects.requireNonNull(accountType);

        this.id = new CoaEntryId(Snowflake.get().nextId());
        this.coa = coa;
        this.category = category;
        this.code(code).name(name).description(description);
        this.accountType = accountType;
        this.createdAt = Instant.now();

        if (this.coa.entries.stream().anyMatch(entry -> entry.getCode().equals(this.code))) {
            throw new CoaEntryCodeAlreadyExistsException(this.code);
        }

        if (this.coa.entries
                .stream()
                .anyMatch(entry -> entry.getName().equalsIgnoreCase(this.name))) {

            throw new CoaEntryNameAlreadyExistsException(this.name, this.coa.name);
        }
    }

    public CoaEntry code(CoaEntryCode code) {

        Objects.requireNonNull(code);

        this.coa.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getCode().equals(code)) {
                throw new CoaEntryCodeAlreadyExistsException(code);
            }
        });

        this.coa.entries.stream().findFirst().ifPresent(existingEntry -> {
            if (existingEntry.getName().equalsIgnoreCase(name)) {
                throw new CoaEntryNameAlreadyExistsException(name, this.coa.name);
            }
        });

        this.code = code;

        return this;
    }

    @Override
    public CoaEntryData convert() {

        return new CoaEntryData(
            this.getId(), this.category, this.code, this.name, this.description, this.accountType,
            this.createdAt, this.coa.getId());
    }

    public CoaEntry description(String description) {

        if (description == null) {
            return this;
        }

        var value = description.trim();

        if (value.length() > StringSizeConstraints.MAX_DESCRIPTION_LENGTH) {
            throw new CoaEntryDescriptionTooLongException();
        }

        this.description = description;

        return this;
    }

    @Override
    public CoaEntryId getId() {

        return this.id;
    }

    public CoaEntry name(String name) {

        if (name == null || name.isBlank()) {
            throw new CoaEntryNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new CoaEntryNameTooLongException();
        }

        this.name = value;

        return this;
    }

}

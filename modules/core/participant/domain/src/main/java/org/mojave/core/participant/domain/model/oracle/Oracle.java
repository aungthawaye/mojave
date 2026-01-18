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
package org.mojave.core.participant.domain.model.oracle;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.common.datatype.converter.identifier.participant.OracleIdJavaType;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.OracleId;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.exception.oracle.OracleBaseUrlRequiredException;
import org.mojave.core.participant.contract.exception.oracle.OracleBaseUrlTooLongException;
import org.mojave.core.participant.contract.exception.oracle.OracleNameRequiredException;
import org.mojave.core.participant.contract.exception.oracle.OracleNameTooLongException;
import org.mojave.core.common.datatype.enums.participant.PartyIdType;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_oracle",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pcp_oracle_01_UK",
            columnNames = {"type"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oracle extends JpaEntity<OracleId> implements DataConversion<OracleData> {

    @Id
    @JavaType(OracleIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "oracle_id",
        nullable = false,
        updatable = false)
    protected OracleId id;

    @Column(
        name = "type",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected PartyIdType type;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @Column(
        name = "base_url",
        nullable = false,
        length = StringSizeConstraints.MAX_HTTP_URL_LENGTH)
    protected String baseUrl;

    @Column(
        name = "activation_status",
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(
        name = "termination_status",
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    @Column(
        name = "created_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Oracle(final PartyIdType type, final String name, final String host) {

        assert type != null;
        assert name != null;
        assert host != null;

        this.id = new OracleId(Snowflake.get().nextId());
        this.type(type).name(name).baseUrl(host);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.terminationStatus = TerminationStatus.ALIVE;
        this.createdAt = Instant.now();
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public Oracle baseUrl(final String baseUrl) {

        if (baseUrl == null || baseUrl.isBlank()) {

            throw new OracleBaseUrlRequiredException();
        }

        final var value = baseUrl.trim();

        if (value.length() > StringSizeConstraints.MAX_HTTP_URL_LENGTH) {

            throw new OracleBaseUrlTooLongException();
        }

        this.baseUrl = value;

        return this;
    }

    @Override
    public OracleData convert() {

        return new OracleData(
            this.id, this.type, this.name, this.baseUrl, this.activationStatus,
            this.terminationStatus);
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    @Override
    public OracleId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    public boolean isTerminated() {

        return this.terminationStatus == TerminationStatus.TERMINATED;
    }

    public Oracle name(final String name) {

        if (name == null || name.isBlank()) {

            throw new OracleNameRequiredException();
        }

        final var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {

            throw new OracleNameTooLongException();
        }

        this.name = value;

        return this;
    }

    public void terminate() {

        this.terminationStatus = TerminationStatus.TERMINATED;
        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public Oracle type(final PartyIdType type) {

        assert type != null;

        this.type = type;

        return this;
    }

}

/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.participant.domain.model.oracle;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.common.datatype.converter.identifier.participant.OracleIdJavaType;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "pcp_oracle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oracle extends JpaEntity<OracleId> {

    @Id
    @JavaType(OracleIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "oracle_id", nullable = false, updatable = false)
    protected OracleId id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    protected PartyIdType type;

    @Column(name = "name")
    protected String name;

    @Column(name = "baseUrl")
    protected String baseUrl;

    @Column(name = "activation_status", length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "termination_status", length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus = TerminationStatus.ALIVE;

    public Oracle(PartyIdType type, String name, String host) {

        assert type != null;
        assert name != null;
        assert host != null;

        this.id = new OracleId(Snowflake.get().nextId());
        this.type(type).name(name).baseUrl(host);
        this.activationStatus = ActivationStatus.ACTIVE;
        this.terminationStatus = TerminationStatus.ALIVE;
    }

    public void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public Oracle baseUrl(String baseUrl) {

        assert baseUrl != null;

        var value = baseUrl.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Oracle Base URL");
        }

        if (value.length() > StringSizeConstraints.MAX_HTTP_URL_LENGTH) {
            throw new TextTooLargeException("Oracle Base URL", StringSizeConstraints.MAX_HTTP_URL_LENGTH);
        }

        this.baseUrl = value;

        return this;
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

    public Oracle name(String name) {

        assert name != null;

        var value = name.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Oracle Name");
        }

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new TextTooLargeException("Oracle Name", StringSizeConstraints.MAX_NAME_TITLE_LENGTH);
        }

        this.name = value;

        return this;
    }

    public void terminate() {

        if (this.terminationStatus == TerminationStatus.TERMINATED) {
            return;
        }

        this.terminationStatus = TerminationStatus.TERMINATED;
        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public Oracle type(PartyIdType type) {

        assert type != null;

        this.type = type;

        return this;
    }

}

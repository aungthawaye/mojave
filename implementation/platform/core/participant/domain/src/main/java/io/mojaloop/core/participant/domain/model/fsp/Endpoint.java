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

package io.mojaloop.core.participant.domain.model.fsp;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.EndpointId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.exception.CannotActivateEndpointException;
import io.mojaloop.core.participant.domain.component.converter.identifier.EndpointIdJavaType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "pcp_endpoint")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class Endpoint extends JpaEntity<EndpointId> implements DataConversion<FspData.EndpointData> {

    @Id

    @JavaType(EndpointIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "endpoint_id", nullable = false, updatable = false)
    private EndpointId id;

    @Column(name = "type", length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private EndpointType type;

    @Column(name = "base_url", length = StringSizeConstraints.MAX_HTTP_URL_LENGTH)
    private String baseUrl;

    @Column(name = "activation_status", length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "fsp_id")
    private Fsp fsp;

    Endpoint(Fsp fsp, EndpointType type, String baseUrl) {

        assert fsp != null;
        assert type != null;

        this.id = new EndpointId(Snowflake.get().nextId());
        this.fsp = fsp;
        this.type = type;
        this.baseUrl(baseUrl);
        this.createdAt = Instant.now();
    }

    public Endpoint baseUrl(String baseUrl) {

        assert baseUrl != null;

        var value = baseUrl.trim();

        if (value.isEmpty()) {
            throw new BlankOrEmptyInputException("Endpoint Base URL");
        }

        if (value.length() > StringSizeConstraints.MAX_HTTP_URL_LENGTH) {
            throw new TextTooLargeException("Endpoint Base URL", StringSizeConstraints.MAX_HTTP_URL_LENGTH);
        }

        this.baseUrl = value;

        return this;
    }

    @Override
    public FspData.EndpointData convert() {

        return new FspData.EndpointData(this.getId(), this.getType(), this.getBaseUrl());
    }

    @Override
    public EndpointId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    void activate() throws CannotActivateEndpointException {

        if (!this.fsp.isActive()) {

            throw new CannotActivateEndpointException(this.type);
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}

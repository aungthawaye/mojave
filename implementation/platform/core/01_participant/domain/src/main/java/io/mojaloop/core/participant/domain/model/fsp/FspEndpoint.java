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

package io.mojaloop.core.participant.domain.model.fsp;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.participant.FspEndpointIdJavaType;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.enums.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspEndpointId;
import io.mojaloop.core.participant.contract.data.FspEndpointData;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspEndpointException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointBaseUrlRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointBaseUrlTooLongException;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
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
@Table(name = "pcp_fsp_endpoint",
       uniqueConstraints = {@UniqueConstraint(name = "pcp_fsp_endpoint_fsp_endpoint_id_type_UK",
                                              columnNames = {"fsp_endpoint_id", "type"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FspEndpoint extends JpaEntity<FspEndpointId>
    implements DataConversion<FspEndpointData> {

    @Id

    @JavaType(FspEndpointIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_endpoint_id", nullable = false, updatable = false)
    private FspEndpointId id;

    @Column(name = "type", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private EndpointType type;

    @Column(name = "base_url", nullable = false, length = StringSizeConstraints.MAX_HTTP_URL_LENGTH)
    private String baseUrl;

    @Column(name = "activation_status",
            nullable = false,
            length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "fsp_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "fsp_endpoint_fsp_FK"))
    private Fsp fsp;

    public FspEndpoint(Fsp fsp, EndpointType type, String baseUrl) {

        assert fsp != null;
        assert type != null;

        this.id = new FspEndpointId(Snowflake.get().nextId());
        this.fsp = fsp;
        this.type = type;
        this.baseUrl(baseUrl);
        this.createdAt = Instant.now();

        if (this.fsp.hasEndpoint(type)) {

            throw new FspEndpointAlreadyConfiguredException(type);
        }
    }

    public FspEndpoint baseUrl(String baseUrl) {

        if (baseUrl == null || baseUrl.isBlank()) {

            throw new FspEndpointBaseUrlRequiredException();
        }

        var value = baseUrl.trim();

        if (value.length() > StringSizeConstraints.MAX_HTTP_URL_LENGTH) {
            throw new FspEndpointBaseUrlTooLongException();
        }

        this.baseUrl = value;

        return this;
    }

    @Override
    public FspEndpointData convert() {

        return new FspEndpointData(
            this.getId(), this.getType(), this.getBaseUrl(), this.createdAt, this.fsp.getId());
    }

    @Override
    public FspEndpointId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    void activate() {

        if (!this.fsp.isActive()) {

            throw new CannotActivateFspEndpointException(this.fsp.fspCode, this.type);
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}

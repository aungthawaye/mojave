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
import io.mojaloop.core.common.datatype.converter.identifier.participant.FspCurrencyIdJavaType;
import io.mojaloop.core.common.datatype.enums.ActivationStatus;
import io.mojaloop.core.common.datatype.identifier.participant.FspCurrencyId;
import io.mojaloop.core.participant.contract.data.FspCurrencyData;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspCurrencyException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.fspiop.spec.core.Currency;
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
@Table(name = "pcp_fsp_currency", uniqueConstraints = {@UniqueConstraint(name = "pcp_fsp_currency_fsp_currency_id_currency_UK", columnNames = {"fsp_currency_id", "currency"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class FspCurrency extends JpaEntity<FspCurrencyId> implements DataConversion<FspCurrencyData> {

    @Id
    @JavaType(FspCurrencyIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_currency_id")
    private FspCurrencyId id;

    @Column(name = "currency", nullable = false, length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "activation_status", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "fsp_id", nullable = false, foreignKey = @ForeignKey(name = "fsp_currency_fsp_FK"))
    private Fsp fsp;

    public FspCurrency(Fsp fsp, Currency currency) {

        assert fsp != null;
        assert currency != null;

        this.id = new FspCurrencyId(Snowflake.get().nextId());
        this.fsp = fsp;
        this.currency = currency;
        this.createdAt = Instant.now();

        if (this.fsp.isCurrencySupported(currency)) {

            throw new FspCurrencyAlreadySupportedException(currency);
        }

        if (!this.fsp.getHub().isCurrencySupported(currency)) {

            throw new FspCurrencyNotSupportedByHubException(currency);
        }
    }

    @Override
    public FspCurrencyData convert() {

        return new FspCurrencyData(this.getId(), this.getCurrency(), this.getActivationStatus(), this.createdAt, this.fsp.getId());
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    void activate() {

        if (!this.fsp.isActive()) {

            throw new CannotActivateFspCurrencyException(this.fsp.fspCode, this.currency);
        }

        if (!this.fsp.getHub().isCurrencySupported(this.currency)) {

            throw new FspCurrencyNotSupportedByHubException(this.currency);
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}

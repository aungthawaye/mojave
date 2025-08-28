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

package io.mojaloop.core.participant.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.core.common.datatype.identifier.participant.SupportedCurrencyId;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Getter
@Entity
@Table(name = "pcp_supported_currency")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SupportedCurrency extends JpaEntity<SupportedCurrencyId> implements DataConversion<FspData.SupportedCurrencyData> {

    @EmbeddedId
    private SupportedCurrencyId id;

    @Column(name = "currency", length = StringSizeConstraints.MAX_CURRENCY_LEN)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(name = "activation_status", length = StringSizeConstraints.MAX_COMMON_ENUM_LEN)
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "fsp_id")
    private Fsp fsp;

    SupportedCurrency(Fsp fsp, Currency currency) {

        assert fsp != null;
        assert currency != null;

        this.id = new SupportedCurrencyId(Snowflake.get().nextId());
        this.fsp = fsp;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    @Override
    public FspData.SupportedCurrencyData convert() {

        return new FspData.SupportedCurrencyData(this.getId(), this.getCurrency(), this.getActivationStatus());
    }

    @Override
    public SupportedCurrencyId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    void activate() throws CannotActivateSupportedCurrencyException {

        if (!this.fsp.isActive()) {

            throw new CannotActivateSupportedCurrencyException(this.currency);
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}

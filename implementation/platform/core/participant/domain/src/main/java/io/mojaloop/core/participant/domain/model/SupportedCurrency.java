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

import io.mojaloop.common.component.handy.Snowflake;
import io.mojaloop.common.component.persistence.JpaEntity;
import io.mojaloop.common.component.persistence.JpaInstantConverter;
import io.mojaloop.common.datatype.enumeration.ActivationStatus;
import io.mojaloop.common.datatype.enumeration.TerminationStatus;
import io.mojaloop.common.datatype.identifier.participant.SupportedCurrencyId;
import io.mojaloop.common.fspiop.model.core.Currency;
import io.mojaloop.core.participant.contract.exception.CannotActivateSupportedCurrencyException;
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
public class SupportedCurrency extends JpaEntity<SupportedCurrencyId> {

    @EmbeddedId
    protected SupportedCurrencyId id;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "activation_status")
    @Enumerated(EnumType.STRING)
    protected ActivationStatus activationStatus;

    @Column(name = "termination_status")
    @Enumerated(EnumType.STRING)
    protected TerminationStatus terminationStatus;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne
    @JoinColumn(name = "fsp_id")
    protected Fsp fsp;

    SupportedCurrency(Fsp fsp, Currency currency) {

        assert fsp != null;
        assert currency != null;

        this.id = new SupportedCurrencyId(Snowflake.get().nextId());
        this.fsp = fsp;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    public void activate() throws CannotActivateSupportedCurrencyException {

        if (!this.fsp.isActive()) {
            throw new CannotActivateSupportedCurrencyException(this.currency.name());
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    @Override
    public SupportedCurrencyId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

}

/*-
 * ==============================================================================
 * Mojave
 * -----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * -----------------------------------------------------------------------------
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
 * ==============================================================================
 */

package org.mojave.core.participant.domain.model.ssp;

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
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.common.datatype.converter.identifier.participant.SspCurrencyIdJavaType;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.identifier.participant.SspCurrencyId;
import org.mojave.core.participant.contract.data.SspCurrencyData;
import org.mojave.core.participant.contract.exception.ssp.SspCurrencyNotSupportedByHubException;
import org.mojave.fspiop.spec.core.Currency;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_ssp_currency",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pcp_ssp_currency_ssp_currency_id_currency_UK",
            columnNames = {
                "ssp_currency_id",
                "currency"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class SspCurrency extends JpaEntity<SspCurrencyId>
    implements DataConversion<SspCurrencyData> {

    @Id
    @JavaType(SspCurrencyIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "ssp_currency_id")
    private SspCurrencyId id;

    @Column(
        name = "currency",
        nullable = false,
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH)
    @Enumerated(EnumType.STRING)
    private Currency currency;

    @Column(
        name = "activation_status",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    private ActivationStatus activationStatus = ActivationStatus.ACTIVE;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(
        name = "ssp_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "ssp_currency_ssp_FK"))
    private Ssp ssp;

    public SspCurrency(final Ssp ssp, final Currency currency) {

        assert ssp != null;
        assert currency != null;

        this.id = new SspCurrencyId(Snowflake.get().nextId());
        this.ssp = ssp;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    public void activate() {

        if (!this.ssp.isActive()) {

            this.activationStatus = ActivationStatus.INACTIVE;
            return;
        }

        if (!this.ssp.getHub().isCurrencySupported(this.currency)) {

            throw new SspCurrencyNotSupportedByHubException(this.currency);
        }

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    @Override
    public SspCurrencyData convert() {

        return new SspCurrencyData(
            this.getId(), this.getCurrency(), this.getActivationStatus(), this.createdAt,
            this.ssp.getId());
    }

    public void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

}

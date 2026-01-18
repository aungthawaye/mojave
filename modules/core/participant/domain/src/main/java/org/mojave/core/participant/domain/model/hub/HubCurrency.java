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

package org.mojave.core.participant.domain.model.hub;

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
import org.mojave.core.common.datatype.converter.identifier.participant.HubCurrencyIdJavaType;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.identifier.participant.HubCurrencyId;
import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.common.datatype.enums.Currency;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_hub_currency",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "pcp_hub_currency_01_UK",
            columnNames = {
                "hub_id",
                "currency"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public final class HubCurrency extends JpaEntity<HubCurrencyId>
    implements DataConversion<HubData.HubCurrencyData> {

    @Id
    @JavaType(HubCurrencyIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "hub_currency_id")
    private HubCurrencyId id;

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

    @Column(
        name = "created_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    private Instant createdAt;

    @ManyToOne
    @JoinColumn(
        name = "hub_id",
        nullable = false,
        foreignKey = @ForeignKey(name = "pcp_hub_pcp_hub_currency_FK"))
    private Hub hub;

    HubCurrency(final Hub hub, final Currency currency) {

        assert hub != null;
        assert currency != null;

        this.id = new HubCurrencyId(Snowflake.get().nextId());
        this.hub = hub;
        this.currency = currency;
        this.createdAt = Instant.now();
    }

    @Override
    public HubData.HubCurrencyData convert() {

        return new HubData.HubCurrencyData(
            this.getId(), this.getCurrency(), this.getActivationStatus());
    }

    @Override
    public HubCurrencyId getId() {

        return this.id;
    }

    public boolean isActive() {

        return this.activationStatus == ActivationStatus.ACTIVE;
    }

    void activate() {

        this.activationStatus = ActivationStatus.ACTIVE;
    }

    void deactivate() {

        this.activationStatus = ActivationStatus.INACTIVE;
    }

}

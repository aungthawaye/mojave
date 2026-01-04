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
package org.mojave.core.wallet.domain.model;

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
import org.mojave.scheme.common.datatype.converter.identifier.wallet.PositionIdJavaType;
import org.mojave.scheme.common.datatype.converter.identifier.wallet.WalletOwnerIdConverter;
import org.mojave.scheme.common.datatype.identifier.wallet.PositionId;
import org.mojave.scheme.common.datatype.identifier.wallet.WalletOwnerId;
import org.mojave.core.wallet.contract.data.PositionData;
import org.mojave.core.wallet.contract.exception.position.FailedToUpdateNdcException;
import org.mojave.scheme.fspiop.core.Currency;

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "wlt_position",
    uniqueConstraints = @UniqueConstraint(
        name = "wlt_position_01_UK",
        columnNames = {
            "wallet_owner_id",
            "currency"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Position extends JpaEntity<PositionId> implements DataConversion<PositionData> {

    @Id
    @JavaType(PositionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "position_id")
    protected PositionId id;

    @Column(name = "wallet_owner_id")
    @Convert(converter = WalletOwnerIdConverter.class)
    protected WalletOwnerId walletOwnerId;

    @Column(
        name = "currency",
        length = StringSizeConstraints.MAX_CURRENCY_LENGTH,
        nullable = false,
        updatable = false)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "name",
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH,
        nullable = false,
        updatable = false)
    protected String name;

    @Column(
        name = "position",
        nullable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal position = BigDecimal.ZERO;

    @Column(
        name = "reserved",
        nullable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal reserved = BigDecimal.ZERO;

    @Column(
        name = "ndc",
        nullable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal ndc = BigDecimal.ZERO;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Position(final WalletOwnerId walletOwnerId, final Currency currency, final String name) {

        assert walletOwnerId != null;
        assert currency != null;
        assert name != null;

        this.id = new PositionId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.name = name;
        this.createdAt = Instant.now();
    }

    public Position(final WalletOwnerId walletOwnerId,
                    final Currency currency,
                    final String name,
                    final BigDecimal ndc) {

        assert walletOwnerId != null;
        assert currency != null;
        assert name != null;

        this.id = new PositionId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.name = name;
        this.createdAt = Instant.now();
        this.ndc = ndc == null ? BigDecimal.ZERO : ndc;
    }

    @Override
    public PositionData convert() {

        return new PositionData(
            this.id, this.walletOwnerId, this.currency, this.name, this.position, this.reserved,
            this.ndc, this.createdAt);
    }

    @Override
    public PositionId getId() {

        return this.id;
    }

    public void updateNdc(BigDecimal ndc) throws FailedToUpdateNdcException {

        assert ndc != null;

        if (ndc.compareTo(BigDecimal.ZERO) < 0) {
            ndc = BigDecimal.ZERO;
        }

        var total = this.position.add(this.reserved);

        if (ndc.subtract(total).signum() < 0) {

            throw new FailedToUpdateNdcException(ndc, this.position, this.reserved);
        }

        this.ndc = ndc;
    }

}

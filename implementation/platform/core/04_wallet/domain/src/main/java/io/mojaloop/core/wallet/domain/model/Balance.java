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

package io.mojaloop.core.wallet.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.BalanceIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.WalletOwnerIdConverter;
import io.mojaloop.core.common.datatype.identifier.wallet.BalanceId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.BalanceData;
import io.mojaloop.fspiop.spec.core.Currency;
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

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "wlt_balance",
    uniqueConstraints = @UniqueConstraint(
        name = "wlt_balance_wallet_owner_id_currency_UK",
        columnNames = {
            "wallet_owner_id",
            "currency"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Balance extends JpaEntity<BalanceId> implements DataConversion<BalanceData> {

    @Id
    @JavaType(BalanceIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "balance_id")
    protected BalanceId id;

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
        name = "balance",
        nullable = false,
        precision = 34,
        scale = 4)
    protected BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public Balance(WalletOwnerId walletOwnerId, Currency currency, String name) {

        assert walletOwnerId != null;
        assert currency != null;
        assert name != null;

        this.id = new BalanceId(Snowflake.get().nextId());
        this.walletOwnerId = walletOwnerId;
        this.currency = currency;
        this.name = name;
        this.createdAt = Instant.now();
    }

    @Override
    public BalanceData convert() {

        return new BalanceData(
            this.id, this.walletOwnerId, this.currency, this.name, this.balance, this.createdAt);
    }

    @Override
    public BalanceId getId() {

        return this.id;
    }

}

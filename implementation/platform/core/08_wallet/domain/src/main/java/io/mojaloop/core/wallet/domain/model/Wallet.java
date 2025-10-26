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
import io.mojaloop.core.common.datatype.converter.identifier.wallet.WalletIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.WalletOwnerIdConverter;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
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
@Table(name = "wlt_wallet",
       uniqueConstraints = @UniqueConstraint(name = "wlt_wallet_owner_id_currency_UK",
                                             columnNames = {"wallet_owner_id", "currency"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Wallet extends JpaEntity<WalletId> {

    @Id
    @JavaType(WalletIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "wallet_id")
    protected WalletId id;

    @Column(name = "wallet_owner_id")
    @Convert(converter = WalletOwnerIdConverter.class)
    protected WalletOwnerId walletOwnerId;

    @Column(name = "currency", length = StringSizeConstraints.MAX_CURRENCY_LENGTH, nullable = false, updatable = false)
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "name", length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH, nullable = false, updatable = false)
    protected String name;

    @Column(name = "balance", nullable = false, precision = 34, scale = 4)
    protected BigDecimal balance = BigDecimal.ZERO;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Override
    public WalletId getId() {

        return this.id;
    }

}

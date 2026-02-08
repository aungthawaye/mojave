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
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.transaction.TransactionIdConverter;
import org.mojave.common.datatype.converter.identifier.wallet.BalanceIdConverter;
import org.mojave.common.datatype.converter.identifier.wallet.BalanceUpdateIdConverter;
import org.mojave.common.datatype.converter.identifier.wallet.BalanceUpdateIdJavaType;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.enums.wallet.BalanceAction;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.common.datatype.identifier.wallet.BalanceId;
import org.mojave.common.datatype.identifier.wallet.BalanceUpdateId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.core.wallet.contract.data.BalanceUpdateData;

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "wlt_balance_update",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "wlt_balance_update_01_UK",
            columnNames = {
                "balance_id",
                "action",
                "transaction_id"}),
        @UniqueConstraint(
            name = "wlt_balance_update_02_UK",
            columnNames = {"withdraw_id"})},
    indexes = {
        @Index(
            name = "wlt_balance_update_01_IDX",
            columnList = "balance_id, action, transaction_at"),
        @Index(
            name = "wlt_balance_update_02_IDX",
            columnList = "transaction_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class BalanceUpdate extends JpaEntity<BalanceUpdateId>
    implements DataConversion<BalanceUpdateData> {

    @Id
    @JavaType(BalanceUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "balance_update_id")
    protected BalanceUpdateId id;

    @Column(name = "balance_id")
    @Convert(converter = BalanceIdConverter.class)
    protected BalanceId balanceId;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    protected BalanceAction action;

    @Column(name = "transaction_id")
    @Convert(converter = TransactionIdConverter.class)
    protected TransactionId transactionId;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(
        name = "amount",
        precision = 34,
        scale = 4,
        nullable = false,
        updatable = false)
    protected BigDecimal amount;

    @Column(
        name = "old_balance",
        precision = 34,
        scale = 4,
        nullable = false,
        updatable = false)
    protected BigDecimal oldBalance;

    @Column(
        name = "new_balance",
        precision = 34,
        scale = 4,
        nullable = false,
        updatable = false)
    protected BigDecimal newBalance;

    @Column(
        name = "description",
        length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "transaction_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Column(name = "withdraw_id")
    @Convert(converter = BalanceUpdateIdConverter.class)
    protected BalanceUpdateId withdrawId;

    @Override
    public BalanceUpdateData convert() {

        return new BalanceUpdateData(
            this.id, this.balanceId, this.action, this.transactionId, this.currency, this.amount,
            this.oldBalance, this.newBalance, this.description, this.transactionAt, this.createdAt,
            this.withdrawId);
    }

    @Override
    public BalanceUpdateId getId() {

        return this.id;
    }

}

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
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.NetDebitCapUpdateIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionIdConverter;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.NetDebitCapUpdateId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
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
    name = "wlt_net_debit_cap_update",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "wlt_net_debit_cap_update_transaction_id_UK",
            columnNames = {"transaction_id"})},
    indexes = {
        @Index(
            name = "wlt_net_debit_cap_update_position_id_transaction_at_IDX",
            columnList = "position_id, transaction_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class NdcUpdate extends JpaEntity<NetDebitCapUpdateId> {

    @Id
    @JavaType(NetDebitCapUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "net_debit_cap_update_id",
        nullable = false,
        updatable = false)
    protected NetDebitCapUpdateId id;

    @Column(name = "position_id")
    @Convert(converter = PositionIdConverter.class)
    protected PositionId positionId;

    @Column(name = "transaction_id")
    @Convert(converter = TransactionIdConverter.class)
    protected TransactionId transactionId;

    @Column(
        name = "old_net_debit_cap",
        precision = 34,
        scale = 4,
        nullable = false,
        updatable = false)
    protected BigDecimal oldNetDebitCap;

    @Column(
        name = "new_net_debit_cap",
        precision = 34,
        scale = 4,
        nullable = false)
    protected BigDecimal newNetDebitCap;

    @Column(
        name = "transaction_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

}

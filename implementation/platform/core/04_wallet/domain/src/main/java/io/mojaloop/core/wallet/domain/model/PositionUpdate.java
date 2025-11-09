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
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionUpdateIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.wallet.PositionUpdateIdJavaType;
import io.mojaloop.core.common.datatype.enums.wallet.PositionAction;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.PositionUpdateId;
import io.mojaloop.core.wallet.contract.data.PositionUpdateData;
import io.mojaloop.fspiop.spec.core.Currency;
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

import java.math.BigDecimal;
import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "wlt_position_update",
       uniqueConstraints = {@UniqueConstraint(name = "wlt_position_update_position_id_action_transaction_id_UK", columnNames = {"position_id", "action", "transaction_id"}),
                            @UniqueConstraint(name = "wlt_balance_update_reversed_id_UK", columnNames = {"reservation_id"})},
       indexes = {@Index(name = "wlt_position_update_position_id_action_transaction_at_idx", columnList = "position_id, action, transaction_at"),
                  @Index(name = "wlt_position_update_transaction_at_idx", columnList = "transaction_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class PositionUpdate extends JpaEntity<PositionUpdateId> implements DataConversion<PositionUpdateData> {

    @Id
    @JavaType(PositionUpdateIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "position_update_id")
    protected PositionUpdateId id;

    @Column(name = "position_id")
    @Convert(converter = PositionIdConverter.class)
    protected PositionId positionId;

    @Column(name = "action")
    @Enumerated(EnumType.STRING)
    protected PositionAction action;

    @Column(name = "transaction_id")
    @Convert(converter = TransactionIdConverter.class)
    protected TransactionId transactionId;

    @Column(name = "currency")
    @Enumerated(EnumType.STRING)
    protected Currency currency;

    @Column(name = "amount", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal amount;

    @Column(name = "old_position", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal oldPosition;

    @Column(name = "new_position", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal newPosition;

    @Column(name = "old_reserved", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal oldReserved;

    @Column(name = "new_reserved", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal newReserved;

    @Column(name = "net_debit_cap", precision = 34, scale = 4, nullable = false, updatable = false)
    protected BigDecimal netDebitCap;

    @Column(name = "description", length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String description;

    @Column(name = "transaction_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Column(name = "created_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @Column(name = "reservation_id")
    @Convert(converter = PositionUpdateIdConverter.class)
    protected PositionUpdateId reservationId;

    @Override
    public PositionUpdateData convert() {

        return new PositionUpdateData(
            this.id, this.positionId, this.action, this.transactionId, this.currency, this.amount, this.oldPosition, this.newPosition, this.description, this.transactionAt,
            this.createdAt, this.reservationId);
    }

}

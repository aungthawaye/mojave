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
package io.mojaloop.core.account.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.account.AccountIdConverter;
import io.mojaloop.core.common.datatype.converter.identifier.account.LedgerMovementIdJavaType;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdConverter;
import io.mojaloop.core.common.datatype.enums.account.Side;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import jakarta.persistence.AttributeOverride;
import jakarta.persistence.AttributeOverrides;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Embedded;
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
@Table(name = "acc_ledger_movement",
       uniqueConstraints = {@UniqueConstraint(name = "acc_account_account_id_side_transaction_id_UK", columnNames = {"account_id", "side", "transaction_id"}),},
       indexes = {@Index(name = "acc_account_transaction_id_IDX", columnList = "transaction_id"),
                  @Index(name = "acc_account_transaction_at_IDX", columnList = "transaction_at"),
                  @Index(name = "acc_account_account_id_transaction_at_IDX", columnList = "account_id, transaction_at"),
                  @Index(name = "acc_account_account_id", columnList = "account_id"), @Index(name = "acc_account_created_at", columnList = "created_at")})
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class LedgerMovement extends JpaEntity<LedgerMovementId> {

    @Id
    @JavaType(LedgerMovementIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    protected LedgerMovementId id;

    @Column(name = "account_id", nullable = false, updatable = false)
    @Convert(converter = AccountIdConverter.class)
    protected AccountId accountId;

    @Column(name = "side", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Side side;

    @Column(name = "amount", nullable = false, updatable = false)
    protected BigDecimal amount;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "debits", column = @Column(name = "old_debits", precision = 34, scale = 4)),
                         @AttributeOverride(name = "credits", column = @Column(name = "old_credits", precision = 34, scale = 4))})
    protected DrCr oldDrCr;

    @Embedded
    @AttributeOverrides({@AttributeOverride(name = "debits", column = @Column(name = "new_debits", precision = 34, scale = 4)),
                         @AttributeOverride(name = "credits", column = @Column(name = "new_credits", precision = 34, scale = 4))})
    protected DrCr newDrCr;

    @Column(name = "transaction_id", nullable = false, updatable = false)
    @Convert(converter = TransactionIdConverter.class)
    protected TransactionId transactionId;

    @Column(name = "transaction_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant transactionAt;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    public LedgerMovement(LedgerMovementId id,
                          AccountId accountId,
                          Side side,
                          BigDecimal amount,
                          DrCr oldDrCr,
                          DrCr newDrCr,
                          TransactionId transactionId,
                          Instant transactionAt) {

        assert id != null;
        assert accountId != null;
        assert side != null;
        assert amount != null;
        assert oldDrCr != null;
        assert newDrCr != null;
        assert transactionId != null;
        assert transactionAt != null;

        this.id = id;
        this.accountId = accountId;
        this.side = side;
        this.amount = amount;
        this.oldDrCr = oldDrCr;
        this.newDrCr = newDrCr;
        this.transactionId = transactionId;
        this.transactionAt = transactionAt;
        this.createdAt = Instant.now();

    }

    @Override
    public LedgerMovementId getId() {

        return this.id;
    }

}

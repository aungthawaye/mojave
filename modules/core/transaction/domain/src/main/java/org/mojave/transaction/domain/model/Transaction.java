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

package org.mojave.transaction.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import org.mojave.common.datatype.enums.trasaction.TransactionPhase;
import org.mojave.common.datatype.identifier.transaction.TransactionId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.jpa.JpaInstantConverter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.scheme.rule.transaction.scenario.TransactionScenario;
import org.mojave.transaction.contract.data.TransactionData;

import java.time.Instant;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "txn_transaction",
    indexes = {
        @Index(
            name = "txn_transaction_01_IDX",
            columnList = "scenario, phase, open_at"),
        @Index(
            name = "txn_transaction_02_IDX",
            columnList = "scenario, phase, close_at"),
        @Index(
            name = "txn_transaction_03_IDX",
            columnList = "open_at"),
        @Index(
            name = "txn_transaction_04_IDX",
            columnList = "close_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends JpaEntity<TransactionId>
    implements DataConversion<TransactionData> {

    @Id
    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "transaction_id",
        nullable = false,
        updatable = false)
    protected TransactionId id;

    @Column(
        name = "scenario",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionScenario scenario;

    @Column(
        name = "phase",
        nullable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionPhase phase;

    @Column(
        name = "open_at",
        nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant openAt;

    @Column(name = "close_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant closeAt;

    @Column(name = "error")
    protected String error;

    @Column(name = "success")
    protected boolean success = true;

    public Transaction(TransactionId transactionId, TransactionScenario scenario) {

        Objects.requireNonNull(transactionId);
        Objects.requireNonNull(scenario);

        this.id = transactionId;
        this.scenario = scenario;
        this.phase = TransactionPhase.OPEN;
        this.openAt = Instant.now();
    }

    public void close(String error) {

        this.closeAt = Instant.now();
        this.error = error;
        this.phase = TransactionPhase.CLOSE;
        this.success = this.error == null;
    }

    public TransactionData convert() {

        return new TransactionData(
            this.id, this.scenario, this.phase, this.openAt, this.closeAt, this.error,
            this.success);
    }

}

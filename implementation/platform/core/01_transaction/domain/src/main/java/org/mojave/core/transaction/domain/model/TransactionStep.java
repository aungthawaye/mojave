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
package org.mojave.core.transaction.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
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
import org.mojave.core.common.datatype.converter.identifier.transaction.TransactionStepIdJavaType;
import org.mojave.core.common.datatype.enums.trasaction.StepPhase;
import org.mojave.core.common.datatype.identifier.transaction.TransactionStepId;
import org.mojave.core.transaction.contract.data.TransactionStepData;

import java.time.Instant;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "txn_transaction_step",
    indexes = @Index(
        name = "idx_txn_transaction_phase_name_context",
        columnList = "phase,name,context"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionStep extends JpaEntity<TransactionStepId>
    implements DataConversion<TransactionStepData> {

    @Id
    @JavaType(TransactionStepIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "step_id",
        nullable = false,
        updatable = false)
    protected TransactionStepId id;

    @Column(
        name = "phase",
        nullable = false,
        updatable = false,
        length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected StepPhase phase;

    @Column(
        name = "name",
        nullable = false)
    protected String name;

    @Column(name = "context")
    protected String context;

    @Column(
        name = "payload",
        length = StringSizeConstraints.MAX_PARAGRAPH_LENGTH)
    protected String payload;

    @Column(
        name = "created_at",
        nullable = false,
        updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(
        name = "transaction_id",
        nullable = false)
    protected Transaction transaction;

    public TransactionStep(Transaction transaction,
                           StepPhase phase,
                           String name,
                           String context,
                           String payload) {

        assert transaction != null;
        assert phase != null;
        assert name != null;

        this.id = new TransactionStepId(Snowflake.get().nextId());
        this.phase = phase;
        this.name = name;
        this.context = context;
        this.payload = payload;
        this.createdAt = Instant.now();
        this.transaction = transaction;
    }

    public TransactionStepData convert() {

        return new TransactionStepData(
            this.id, this.phase, this.name, this.context, this.payload,
            this.createdAt, this.transaction.getId());
    }

    @Override
    public TransactionStepId getId() {

        return id;
    }

}

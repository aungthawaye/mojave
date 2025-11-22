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

package io.mojaloop.core.transaction.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.jpa.JpaInstantConverter;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionIdJavaType;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionPhase;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import io.mojaloop.core.transaction.contract.data.TransactionData;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.Index;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_transaction",
       indexes = {@Index(name = "txn_transaction_type_phase_open_at_IDX", columnList = "type, phase, open_at"),
                  @Index(name = "txn_transaction_type_phase_close_at_IDX", columnList = "type, phase, close_at"),
                  @Index(name = "txn_transaction_open_at_IDX", columnList = "open_at"),
                  @Index(name = "txn_transaction_close_at_IDX", columnList = "close_at")})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Transaction extends JpaEntity<TransactionId> implements DataConversion<TransactionData> {

    @Id
    @JavaType(TransactionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "transaction_id", nullable = false, updatable = false)
    protected TransactionId id;

    @Column(name = "type", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionType type;

    @Column(name = "phase", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected TransactionPhase phase;

    @Column(name = "open_at", nullable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant openAt;

    @Column(name = "close_at")
    @Convert(converter = JpaInstantConverter.class)
    protected Instant closeAt;

    @Column(name = "error")
    protected String error;

    @Column(name = "success")
    protected Boolean success = true;

    @OneToMany(mappedBy = "transaction", orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    protected List<TransactionStep> steps = new ArrayList<>();

    public Transaction(TransactionId transactionId, TransactionType type) {

        assert transactionId != null;
        assert type != null;

        this.id = transactionId;
        this.type = type;
        this.phase = TransactionPhase.OPEN;
        this.openAt = Instant.now();
    }

    public TransactionStep addStep(StepPhase phase, String name, Map<String, String> params) {

        var step = new TransactionStep(this, phase, name);

        params.forEach(step::addParam);

        this.steps.add(step);

        return step;
    }

    public void close(String error) {

        this.closeAt = Instant.now();
        this.error = error;
        this.phase = TransactionPhase.CLOSE;
        this.success = this.error == null;
    }

    public TransactionData convert() {

        var stepData = this.steps.stream().map(TransactionStep::convert).toList();

        return new TransactionData(this.id, this.type, this.phase, this.openAt, this.closeAt, this.error, this.success, stepData);
    }

    public List<TransactionStep> getSteps() {

        return Collections.unmodifiableList(this.steps);
    }

}

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
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.TransactionStepIdJavaType;
import io.mojaloop.core.common.datatype.enums.trasaction.StepPhase;
import io.mojaloop.core.common.datatype.identifier.transaction.TransactionStepId;
import io.mojaloop.core.transaction.contract.data.TransactionStepData;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Convert;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
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

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_transaction_step")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransactionStep extends JpaEntity<TransactionStepId> implements DataConversion<TransactionStepData> {

    @Id
    @JavaType(TransactionStepIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "step_id", nullable = false, updatable = false)
    protected TransactionStepId id;

    @Column(name = "name", nullable = false)
    protected String name;

    @Column(name = "phase", nullable = false, updatable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected StepPhase phase;

    @Column(name = "created_at", nullable = false, updatable = false)
    @Convert(converter = JpaInstantConverter.class)
    protected Instant createdAt;

    @OneToMany(mappedBy = "step", orphanRemoval = true, cascade = {CascadeType.ALL}, fetch = FetchType.EAGER)
    protected List<StepParam> params = new ArrayList<>();

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "transaction_id", nullable = false)
    protected Transaction transaction;

    public TransactionStep(Transaction transaction, StepPhase phase, String name) {

        assert transaction != null;
        assert phase != null;
        assert name != null;

        this.id = new TransactionStepId(Snowflake.get().nextId());
        this.name = name;
        this.phase = phase;
        this.createdAt = Instant.now();
        this.transaction = transaction;
    }

    public void addParam(String name, String value) {

        this.params.add(new StepParam(this, name, value));
    }

    public TransactionStepData convert() {

        var paramData = this.params.stream().map(StepParam::convert).toList();

        return new TransactionStepData(this.id, this.name, this.phase, this.createdAt, this.transaction.getId(), paramData);
    }

    @Override
    public TransactionStepId getId() {

        return id;
    }

    public List<StepParam> getParams() {

        return Collections.unmodifiableList(this.params);
    }

}

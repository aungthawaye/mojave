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
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.data.DataConversion;
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.transaction.StepParamIdJavaType;
import io.mojaloop.core.common.datatype.identifier.transaction.StepParamId;
import io.mojaloop.core.transaction.contract.data.StepParamData;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "txn_step_param")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class StepParam extends JpaEntity<StepParamId> implements DataConversion<StepParamData> {

    @Id
    @JavaType(StepParamIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "param_id", nullable = false, updatable = false)
    protected StepParamId id;

    @Column(name = "param_name", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String paramName;

    @Column(name = "param_value", nullable = false)
    protected String paramValue;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "step_id", nullable = false)
    protected TransactionStep step;

    public StepParam(TransactionStep step, String paramName, String paramValue) {

        assert step != null;
        assert paramName != null;
        assert paramValue != null;

        this.id = new StepParamId(Snowflake.get().nextId());
        this.paramName = paramName;
        this.paramValue = paramValue;
        this.step = step;

    }

    public StepParamData convert() {

        return new StepParamData(this.id, this.paramName, this.paramValue, this.step.getId());
    }

    @Override
    public StepParamId getId() {

        return this.id;
    }

}

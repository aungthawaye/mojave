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

package io.mojaloop.core.participant.domain.model;

import io.mojaloop.common.component.persistence.JpaEntity;
import io.mojaloop.common.datatype.identifier.participant.OracleId;
import io.mojaloop.common.fspiop.model.core.PartyIdType;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "pcp_oracle")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Oracle extends JpaEntity<OracleId> {

    @EmbeddedId
    protected OracleId id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    protected PartyIdType type;

    @Column(name = "name")
    protected String name;

    @Column(name = "host")
    protected String host;

    public Oracle(PartyIdType type, String name, String host) {

        assert type != null;
        assert name != null;
        assert host != null;

    }

    @Override
    public OracleId getId() {

        return this.id;
    }

}

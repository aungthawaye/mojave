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
import io.mojaloop.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.common.datatype.identifier.participant.EndpointId;
import jakarta.persistence.Column;
import jakarta.persistence.EmbeddedId;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "pcp_endpoint")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Endpoint extends JpaEntity<EndpointId> {

    @EmbeddedId
    protected EndpointId id;

    @Column(name = "type")
    @Enumerated(EnumType.STRING)
    protected EndpointType type;

    @Column(name = "host")
    protected String host;

    @ManyToOne
    @JoinColumn(name = "fsp_id")
    protected Fsp fsp;

    @Override
    public EndpointId getId() {

        return this.id;
    }

}

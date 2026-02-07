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

package org.mojave.core.participant.domain.model.fsp;

import jakarta.persistence.Basic;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.FspGroupItemIdJavaType;
import org.mojave.common.datatype.converter.identifier.participant.FspIdJavaType;
import org.mojave.common.datatype.identifier.participant.FspGroupItemId;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.participant.contract.data.FspGroupItemData;
import org.mojave.core.participant.contract.exception.fsp.FspAlreadyExistsInFspGroupException;

import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_fsp_group_item",
    uniqueConstraints = @UniqueConstraint(
        name = "pcp_fsp_group_item_01_UK",
        columnNames = {
            "fsp_group_id",
            "fsp_id"}))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FspGroupItem extends JpaEntity<FspGroupItemId>
    implements DataConversion<FspGroupItemData> {

    @Id
    @JavaType(FspGroupItemIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_group_item_id")
    protected FspGroupItemId id;

    @Basic
    @JavaType(FspIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "fsp_id",
        nullable = false,
        updatable = false)
    protected FspId fspId;

    @ManyToOne
    @JoinColumn(
        name = "fsp_group_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "pcp_fsp_group_pcp_fsp_group_item_FK"))
    protected FspGroup fspGroup;

    public FspGroupItem(final FspGroup fspGroup, final FspId fspId) {

        Objects.requireNonNull(fspGroup);
        Objects.requireNonNull(fspId);

        this.id = new FspGroupItemId(Snowflake.get().nextId());
        this.fspGroup = fspGroup;

        if (this.fspGroup.fspExists(fspId)) {
            throw new FspAlreadyExistsInFspGroupException();
        }

        this.fspId = fspId;
    }

    @Override
    public FspGroupItemData convert() {

        return new FspGroupItemData(this.id, this.fspId, this.fspGroup.getId());
    }

    public boolean matches(final FspId fspId) {

        return this.fspId.equals(fspId);
    }

}

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

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.common.datatype.converter.identifier.participant.FspGroupIdJavaType;
import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.common.datatype.identifier.participant.FspGroupItemId;
import org.mojave.common.datatype.identifier.participant.FspId;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.data.DataConversion;
import org.mojave.component.misc.handy.Snowflake;
import org.mojave.core.participant.contract.data.FspGroupData;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameTooLongException;

import java.util.ArrayList;
import java.util.List;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "pcp_fsp_group",
    uniqueConstraints = @UniqueConstraint(
        name = "pcp_fsp_group_01_UK",
        columnNames = "name"))
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FspGroup extends JpaEntity<FspGroupId> implements DataConversion<FspGroupData> {

    @Id
    @JavaType(FspGroupIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "fsp_group_id")
    protected FspGroupId id;

    @Column(
        name = "name",
        nullable = false,
        length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String name;

    @OneToMany(
        mappedBy = "fspGroup",
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER)
    protected List<FspGroupItem> items = new ArrayList<>();

    public FspGroup(final String name) {

        this.id = new FspGroupId(Snowflake.get().nextId());
        this.name(name);
    }

    public FspGroupItem addItem(final FspId fspId) {

        final var item = new FspGroupItem(this, fspId);

        this.items.add(item);

        return item;
    }

    @Override
    public FspGroupData convert() {

        final var itemData = this.items.stream().map(FspGroupItem::convert).toList();

        return new FspGroupData(this.id, this.name, itemData);
    }

    public boolean fspExists(final FspId fspId) {

        return this.items.stream().anyMatch(item -> item.matches(fspId));
    }

    @Override
    public FspGroupId getId() {

        return this.id;
    }

    public FspGroup name(final String name) {

        if (name == null || name.isBlank()) {
            throw new FspGroupNameRequiredException();
        }

        var value = name.trim();

        if (value.length() > StringSizeConstraints.MAX_NAME_TITLE_LENGTH) {
            throw new FspGroupNameTooLongException();
        }

        this.name = value;

        return this;
    }

    public boolean removeItem(final FspGroupItemId fspGroupItemId) {

        return this.items.removeIf(item -> fspGroupItemId.equals(item.getId()));
    }

}

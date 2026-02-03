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

package org.mojave.rail.fspiop.transfer.domain.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import jakarta.persistence.UniqueConstraint;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;
import org.mojave.component.jpa.JpaEntity;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.core.common.datatype.converter.identifier.transfer.TransferIdJavaType;
import org.mojave.core.common.datatype.identifier.transfer.TransferId;
import java.util.Objects;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(
    name = "tfr_transfer_ilp_packet",
    uniqueConstraints = {
        @UniqueConstraint(
            name = "tfr_transfer_ilp_packet_ilp_condition_UK",
            columnNames = {"ilp_condition"})})
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class TransferIlpPacket extends JpaEntity<TransferId> {

    @Id
    @JavaType(TransferIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(
        name = "transfer_id",
        nullable = false,
        updatable = false)
    protected TransferId id;

    @Column(
        name = "ilp_packet",
        length = StringSizeConstraints.MAX_ILP_PACKET_LENGTH,
        updatable = false)
    protected String ilpPacket;

    @Column(
        name = "ilp_condition",
        length = StringSizeConstraints.MAX_ILP_PACKET_CONDITION_LENGTH)
    protected String condition;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(
        name = "transfer_id",
        nullable = false,
        updatable = false,
        foreignKey = @ForeignKey(name = "transfer_ilp_packet_transfer_FK"))
    protected Transfer transfer;

    public TransferIlpPacket(Transfer transfer, String ilpPacket, String ilpCondition) {

        Objects.requireNonNull(transfer);
        Objects.requireNonNull(ilpPacket);
        Objects.requireNonNull(ilpCondition);

        this.id = transfer.getId();
        this.ilpPacket = ilpPacket;
        this.condition = ilpCondition;

        this.transfer = transfer;
    }

    @Override
    public TransferId getId() {

        return this.id;
    }

}

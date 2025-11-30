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

package io.mojaloop.core.quoting.domain.model;

import io.mojaloop.component.jpa.JpaEntity;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.QuoteIdJavaType;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.ForeignKey;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.MapsId;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.JavaType;
import org.hibernate.annotations.JdbcTypeCode;

import static java.sql.Types.BIGINT;

@Getter
@Entity
@Table(name = "qot_quote_ilp_packet")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteIlpPacket extends JpaEntity<QuoteId> {

    @Id
    @JavaType(QuoteIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "quote_id", nullable = false, updatable = false)
    protected QuoteId id;

    @Column(name = "ilp_packet", length = StringSizeConstraints.MAX_ILP_PACKET_LENGTH)
    protected String ilpPacket;

    @Column(name = "ilp_condition", length = StringSizeConstraints.MAX_ILP_PACKET_CONDITION_LENGTH)
    protected String condition;

    @MapsId
    @OneToOne
    @JoinColumn(name = "quote_id",
                nullable = false,
                updatable = false,
                foreignKey = @ForeignKey(name = "quote_ilp_packet_quote_FK"))
    protected Quote quote;

    public QuoteIlpPacket(Quote quote) {

        this.id = quote.getId();
        this.quote = quote;
    }

    @Override
    public QuoteId getId() {

        return this.id;
    }

    public void prepared(String ilpPacket, String condition) {

        this.ilpPacket = ilpPacket;
        this.condition = condition;
    }

}

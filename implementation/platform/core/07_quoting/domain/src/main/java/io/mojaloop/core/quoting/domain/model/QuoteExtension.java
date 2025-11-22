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
import io.mojaloop.component.misc.handy.Snowflake;
import io.mojaloop.core.common.datatype.converter.identifier.quoting.QuoteExtensionIdJavaType;
import io.mojaloop.core.common.datatype.enums.Direction;
import io.mojaloop.core.common.datatype.identifier.quoting.QuoteExtensionId;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.ForeignKey;
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
@Table(name = "qot_quote_extension")
@Entity
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class QuoteExtension extends JpaEntity<QuoteExtensionId> {

    @Id
    @JavaType(QuoteExtensionIdJavaType.class)
    @JdbcTypeCode(BIGINT)
    @Column(name = "quote_extension_id", nullable = false, updatable = false)
    protected QuoteExtensionId id;

    @Column(name = "direction", nullable = false, length = StringSizeConstraints.MAX_ENUM_LENGTH)
    @Enumerated(EnumType.STRING)
    protected Direction direction;

    @Column(name = "x_key", nullable = false, length = StringSizeConstraints.MAX_NAME_TITLE_LENGTH)
    protected String key;

    @Column(name = "x_value",
            nullable = false,
            length = StringSizeConstraints.MAX_DESCRIPTION_LENGTH)
    protected String value;

    @ManyToOne
    @JoinColumn(name = "quote_id",
                nullable = false,
                foreignKey = @ForeignKey(name = "quote_extension_quote_FK"))
    protected Quote quote;

    public QuoteExtension(Quote quote, Direction direction, String key, String value) {

        assert quote != null;
        assert direction != null;
        assert key != null;
        assert value != null;

        this.id = new QuoteExtensionId(Snowflake.get().nextId());
        this.direction = direction;
        this.key = key;
        this.value = value;
        this.quote = quote;
    }

    @Override
    public QuoteExtensionId getId() {

        return this.id;
    }

}

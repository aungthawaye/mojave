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
package org.mojave.core.common.datatype.converter.identifier.wallet;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.core.common.datatype.identifier.wallet.PositionUpdateId;

public class PositionUpdateIdJavaType extends AbstractClassJavaType<PositionUpdateId> {

    public static final PositionUpdateIdJavaType INSTANCE = new PositionUpdateIdJavaType();

    public PositionUpdateIdJavaType() {

        super(PositionUpdateId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public PositionUpdateId fromString(CharSequence string) {

        return (string == null) ? null : new PositionUpdateId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(PositionUpdateId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(PositionUpdateId value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        Long primitive = value.getId();

        if (type.isAssignableFrom(Long.class)) {
            return (X) primitive;
        }

        if (type.isAssignableFrom(Number.class)) {
            return (X) primitive;
        }

        throw new IllegalArgumentException("Unsupported unwrap to " + type);
    }

    @Override
    public PositionUpdateId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case PositionUpdateId positionUpdateId -> positionUpdateId;
            case Number n -> new PositionUpdateId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

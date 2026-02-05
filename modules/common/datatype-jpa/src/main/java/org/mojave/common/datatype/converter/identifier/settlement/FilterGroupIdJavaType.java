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

package org.mojave.common.datatype.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.common.datatype.identifier.settlement.FilterGroupId;

public class FilterGroupIdJavaType extends AbstractClassJavaType<FilterGroupId> {

    public static final FilterGroupIdJavaType INSTANCE = new FilterGroupIdJavaType();

    public FilterGroupIdJavaType() {

        super(FilterGroupId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public FilterGroupId fromString(CharSequence string) {

        return (string == null) ? null : new FilterGroupId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(FilterGroupId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(FilterGroupId value, Class<X> type, WrapperOptions options) {

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
    public FilterGroupId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case FilterGroupId filterGroupId -> filterGroupId;
            case Number n -> new FilterGroupId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

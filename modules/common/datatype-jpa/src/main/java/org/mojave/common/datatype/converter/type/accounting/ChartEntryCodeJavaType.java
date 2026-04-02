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

package org.mojave.common.datatype.converter.type.accounting;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;

public class ChartEntryCodeJavaType extends AbstractClassJavaType<CoaEntryCode> {

    public static final ChartEntryCodeJavaType INSTANCE = new ChartEntryCodeJavaType();

    public ChartEntryCodeJavaType() {

        super(CoaEntryCode.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public CoaEntryCode fromString(CharSequence string) {

        return (string == null) ? null : new CoaEntryCode(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(CoaEntryCode value) {

        return value == null ? null : value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(CoaEntryCode value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        String primitive = value.value();

        if (type.isAssignableFrom(String.class)) {
            return (X) primitive;
        }

        throw new IllegalArgumentException("Unsupported unwrap to " + type);
    }

    @Override
    public CoaEntryCode wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case CoaEntryCode code -> code;
            case String s -> new CoaEntryCode(s);
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

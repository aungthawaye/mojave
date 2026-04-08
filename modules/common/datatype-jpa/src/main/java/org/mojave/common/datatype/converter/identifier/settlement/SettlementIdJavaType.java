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
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;
import org.mojave.common.datatype.identifier.settlement.SettlementId;

public class SettlementIdJavaType extends AbstractClassJavaType<SettlementId> {

    public static final SettlementIdJavaType INSTANCE = new SettlementIdJavaType();

    public SettlementIdJavaType() {

        super(SettlementId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementId fromString(CharSequence string) {

        return (string == null) ? null : new SettlementId(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementId value) {

        return value == null ? null : value.getId();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementId value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        String string = value.getId();

        if (type.isAssignableFrom(String.class)) {
            return (X) string;
        }

        throw new IllegalArgumentException("Unsupported unwrap to " + type);
    }

    @Override
    public SettlementId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementId settlementId -> settlementId;
            case String s -> new SettlementId(s);
            case Character c -> new SettlementId(c.toString());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

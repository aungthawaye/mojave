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

package io.mojaloop.core.common.datatype.converter.type.fspiop;

import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class FspCodeJavaType extends AbstractClassJavaType<FspCode> {

    public static final FspCodeJavaType INSTANCE = new FspCodeJavaType();

    public FspCodeJavaType() {

        super(FspCode.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public FspCode fromString(CharSequence string) {

        return (string == null) ? null : new FspCode(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(FspCode value) {

        return value == null ? null : value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(FspCode value, Class<X> type, WrapperOptions options) {

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
    public FspCode wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case FspCode code -> code;
            case String s -> new FspCode(s);
            default -> throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

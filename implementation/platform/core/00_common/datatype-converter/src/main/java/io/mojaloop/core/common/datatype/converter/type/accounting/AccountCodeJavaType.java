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

package io.mojaloop.core.common.datatype.converter.type.accounting;

import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class AccountCodeJavaType extends AbstractClassJavaType<AccountCode> {

    public static final AccountCodeJavaType INSTANCE = new AccountCodeJavaType();

    public AccountCodeJavaType() {

        super(AccountCode.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public AccountCode fromString(CharSequence string) {

        return (string == null) ? null : new AccountCode(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(AccountCode value) {

        return value == null ? null : value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(AccountCode value, Class<X> type, WrapperOptions options) {

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
    public AccountCode wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case AccountCode code -> code;
            case String s -> new AccountCode(s);
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

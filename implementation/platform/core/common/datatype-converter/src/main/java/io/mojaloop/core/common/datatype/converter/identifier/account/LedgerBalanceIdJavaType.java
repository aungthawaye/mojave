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

package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class LedgerBalanceIdJavaType extends AbstractClassJavaType<LedgerBalanceId> {

    public static final LedgerBalanceIdJavaType INSTANCE = new LedgerBalanceIdJavaType();

    public LedgerBalanceIdJavaType() {

        super(LedgerBalanceId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public LedgerBalanceId fromString(CharSequence string) {

        return (string == null) ? null : new LedgerBalanceId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(LedgerBalanceId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(LedgerBalanceId value, Class<X> type, WrapperOptions options) {

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
    public LedgerBalanceId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof LedgerBalanceId id) {
            return id;
        }

        if (value instanceof Number n) {
            return new LedgerBalanceId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.ChartEntryDefinitionId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class ChartEntryDefinitionIdJavaType extends AbstractClassJavaType<ChartEntryDefinitionId> {

    public static final ChartEntryDefinitionIdJavaType INSTANCE = new ChartEntryDefinitionIdJavaType();

    public ChartEntryDefinitionIdJavaType() {

        super(ChartEntryDefinitionId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public ChartEntryDefinitionId fromString(CharSequence string) {

        return (string == null) ? null : new ChartEntryDefinitionId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(ChartEntryDefinitionId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(ChartEntryDefinitionId value, Class<X> type, WrapperOptions options) {

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
    public ChartEntryDefinitionId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof ChartEntryDefinitionId id) {
            return id;
        }

        if (value instanceof Number n) {
            return new ChartEntryDefinitionId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }
}

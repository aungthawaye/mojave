package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.SupportedCurrencyId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class SupportedCurrencyIdJavaType extends AbstractClassJavaType<SupportedCurrencyId> {

    public static final SupportedCurrencyIdJavaType INSTANCE = new SupportedCurrencyIdJavaType();

    public SupportedCurrencyIdJavaType() {

        super(SupportedCurrencyId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SupportedCurrencyId fromString(CharSequence string) {

        return (string == null) ? null : new SupportedCurrencyId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SupportedCurrencyId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SupportedCurrencyId value, Class<X> type, WrapperOptions options) {

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
    public SupportedCurrencyId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof SupportedCurrencyId supportedCurrencyId) {
            return supportedCurrencyId;
        }

        if (value instanceof Number n) {
            return new SupportedCurrencyId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

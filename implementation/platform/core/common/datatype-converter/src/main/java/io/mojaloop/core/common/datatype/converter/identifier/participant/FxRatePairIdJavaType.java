package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.FxRatePairId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class FxRatePairIdJavaType extends AbstractClassJavaType<FxRatePairId> {

    public static final FxRatePairIdJavaType INSTANCE = new FxRatePairIdJavaType();

    public FxRatePairIdJavaType() {

        super(FxRatePairId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public FxRatePairId fromString(CharSequence string) {

        return (string == null) ? null : new FxRatePairId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(FxRatePairId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(FxRatePairId value, Class<X> type, WrapperOptions options) {

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
    public FxRatePairId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof FxRatePairId fxRatePairId) {
            return fxRatePairId;
        }

        if (value instanceof Number n) {
            return new FxRatePairId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

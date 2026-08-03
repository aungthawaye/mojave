package org.mojave.scheme.rule.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;

public class SettlementWindowIdJavaType extends AbstractClassJavaType<SettlementWindowId> {

    public static final SettlementWindowIdJavaType INSTANCE = new SettlementWindowIdJavaType();

    public SettlementWindowIdJavaType() {

        super(SettlementWindowId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementWindowId fromString(CharSequence string) {

        return string == null ? null : new SettlementWindowId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementWindowId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementWindowId value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        final Long primitive = value.getId();

        if (type.isAssignableFrom(Long.class)) {
            return (X) primitive;
        }

        if (type.isAssignableFrom(Number.class)) {
            return (X) primitive;
        }

        throw new IllegalArgumentException("Unsupported unwrap to " + type);
    }

    @Override
    public SettlementWindowId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementWindowId settlementWindowId -> settlementWindowId;
            case Number n -> new SettlementWindowId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };
    }

}

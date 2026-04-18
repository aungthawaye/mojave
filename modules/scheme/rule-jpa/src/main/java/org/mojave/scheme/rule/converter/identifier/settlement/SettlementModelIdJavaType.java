package org.mojave.scheme.rule.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;

public class SettlementModelIdJavaType extends AbstractClassJavaType<SettlementModelId> {

    public static final SettlementModelIdJavaType INSTANCE = new SettlementModelIdJavaType();

    public SettlementModelIdJavaType() {

        super(SettlementModelId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementModelId fromString(CharSequence string) {

        return (string == null) ? null : new SettlementModelId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementModelId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementModelId value, Class<X> type, WrapperOptions options) {

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
    public SettlementModelId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementModelId settlementModelId -> settlementModelId;
            case Number n -> new SettlementModelId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

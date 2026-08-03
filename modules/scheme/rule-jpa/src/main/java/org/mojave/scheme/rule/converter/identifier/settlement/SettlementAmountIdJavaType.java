package org.mojave.scheme.rule.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.scheme.rule.identifier.settlement.SettlementAmountId;

public class SettlementAmountIdJavaType extends AbstractClassJavaType<SettlementAmountId> {

    public static final SettlementAmountIdJavaType INSTANCE = new SettlementAmountIdJavaType();

    public SettlementAmountIdJavaType() {

        super(SettlementAmountId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementAmountId fromString(CharSequence string) {

        return string == null ? null : new SettlementAmountId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementAmountId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementAmountId value, Class<X> type, WrapperOptions options) {

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
    public SettlementAmountId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementAmountId settlementAmountId -> settlementAmountId;
            case Number n -> new SettlementAmountId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };
    }

}

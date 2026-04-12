package org.mojave.scheme.rule.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.scheme.rule.identifier.settlement.SettlementBatchId;

public class SettlementBatchIdJavaType extends AbstractClassJavaType<SettlementBatchId> {

    public static final SettlementBatchIdJavaType INSTANCE = new SettlementBatchIdJavaType();

    public SettlementBatchIdJavaType() {

        super(SettlementBatchId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementBatchId fromString(CharSequence string) {

        return (string == null) ? null : new SettlementBatchId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementBatchId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementBatchId value, Class<X> type, WrapperOptions options) {

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
    public SettlementBatchId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementBatchId settlementBatchId -> settlementBatchId;
            case Number n -> new SettlementBatchId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

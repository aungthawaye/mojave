package org.mojave.common.datatype.converter.identifier.settlement;

import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.mojave.common.datatype.identifier.settlement.SettlementInstructionId;

public class SettlementInstructionIdJavaType extends AbstractClassJavaType<SettlementInstructionId> {

    public static final SettlementInstructionIdJavaType INSTANCE = new SettlementInstructionIdJavaType();

    public SettlementInstructionIdJavaType() {

        super(SettlementInstructionId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public SettlementInstructionId fromString(CharSequence string) {

        return (string == null) ? null : new SettlementInstructionId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(SettlementInstructionId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(SettlementInstructionId value, Class<X> type, WrapperOptions options) {

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
    public SettlementInstructionId wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case SettlementInstructionId settlementInstructionId -> settlementInstructionId;
            case Number n -> new SettlementInstructionId(n.longValue());
            default ->
                throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }

}

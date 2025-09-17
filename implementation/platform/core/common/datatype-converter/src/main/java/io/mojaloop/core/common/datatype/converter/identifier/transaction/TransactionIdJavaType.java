package io.mojaloop.core.common.datatype.converter.identifier.transaction;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class TransactionIdJavaType extends AbstractClassJavaType<TransactionId> {

    public static final TransactionIdJavaType INSTANCE = new TransactionIdJavaType();

    public TransactionIdJavaType() {

        super(TransactionId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public TransactionId fromString(CharSequence string) {

        return (string == null) ? null : new TransactionId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(TransactionId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(TransactionId value, Class<X> type, WrapperOptions options) {

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
    public TransactionId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof TransactionId transactionId) {
            return transactionId;
        }

        if (value instanceof Number n) {
            return new TransactionId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

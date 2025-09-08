package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.JournalEntryId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class JournalEntryIdJavaType extends AbstractClassJavaType<JournalEntryId> {

    public static final JournalEntryIdJavaType INSTANCE = new JournalEntryIdJavaType();

    public JournalEntryIdJavaType() {

        super(JournalEntryId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public JournalEntryId fromString(CharSequence string) {

        return (string == null) ? null : new JournalEntryId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(JournalEntryId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(JournalEntryId value, Class<X> type, WrapperOptions options) {

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
    public JournalEntryId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof JournalEntryId id) {
            return id;
        }

        if (value instanceof Number n) {
            return new JournalEntryId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }
}

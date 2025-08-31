package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class OracleIdJavaType extends AbstractClassJavaType<OracleId> {

    public static final OracleIdJavaType INSTANCE = new OracleIdJavaType();

    public OracleIdJavaType() {

        super(OracleId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public OracleId fromString(CharSequence string) {

        return (string == null) ? null : new OracleId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(OracleId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(OracleId value, Class<X> type, WrapperOptions options) {

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
    public OracleId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof OracleId oracleId) {
            return oracleId;
        }

        if (value instanceof Number n) {
            return new OracleId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

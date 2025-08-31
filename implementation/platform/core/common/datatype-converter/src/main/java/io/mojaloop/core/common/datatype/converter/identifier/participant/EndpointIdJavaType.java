package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.EndpointId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class EndpointIdJavaType extends AbstractClassJavaType<EndpointId> {

    public static final EndpointIdJavaType INSTANCE = new EndpointIdJavaType();

    public EndpointIdJavaType() {

        super(EndpointId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public EndpointId fromString(CharSequence string) {

        return (string == null) ? null : new EndpointId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(EndpointId value) {

        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(EndpointId value, Class<X> type, WrapperOptions options) {

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
    public EndpointId wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof EndpointId endpointId) {
            return endpointId;
        }

        if (value instanceof Number n) {
            return new EndpointId(n.longValue());
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }

}

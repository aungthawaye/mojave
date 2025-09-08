package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.BigIntJdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;

public class HubIdJavaType extends AbstractClassJavaType<HubId> {

    public static final HubIdJavaType INSTANCE = new HubIdJavaType();

    public HubIdJavaType() {
        super(HubId.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public HubId fromString(CharSequence string) {
        return (string == null) ? null : new HubId(Long.valueOf(string.toString()));
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {
        return BigIntJdbcType.INSTANCE;
    }

    @Override
    public String toString(HubId value) {
        return value == null ? null : String.valueOf(value.getId());
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(HubId value, Class<X> type, WrapperOptions options) {
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
    public HubId wrap(Object value, WrapperOptions options) {
        if (value == null) {
            return null;
        }
        if (value instanceof HubId hubId) {
            return hubId;
        }
        if (value instanceof Number n) {
            return new HubId(n.longValue());
        }
        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }
}

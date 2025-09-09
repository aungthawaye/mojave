package io.mojaloop.core.common.datatype.converter.type.account;

import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class ChartEntryCodeJavaType extends AbstractClassJavaType<ChartEntryCode> {

    public static final ChartEntryCodeJavaType INSTANCE = new ChartEntryCodeJavaType();

    public ChartEntryCodeJavaType() {

        super(ChartEntryCode.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public ChartEntryCode fromString(CharSequence string) {

        return (string == null) ? null : new ChartEntryCode(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(ChartEntryCode value) {

        return value == null ? null : value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(ChartEntryCode value, Class<X> type, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        String primitive = value.value();

        if (type.isAssignableFrom(String.class)) {
            return (X) primitive;
        }

        throw new IllegalArgumentException("Unsupported unwrap to " + type);
    }

    @Override
    public ChartEntryCode wrap(Object value, WrapperOptions options) {

        return switch (value) {
            case null -> null;
            case ChartEntryCode code -> code;
            case String s -> new ChartEntryCode(s);
            default -> throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
        };

    }
}

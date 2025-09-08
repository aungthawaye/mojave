package io.mojaloop.core.common.datatype.converter.type.account;

import io.mojaloop.core.common.datatype.type.account.AccountCode;
import org.hibernate.type.descriptor.WrapperOptions;
import org.hibernate.type.descriptor.java.AbstractClassJavaType;
import org.hibernate.type.descriptor.java.ImmutableMutabilityPlan;
import org.hibernate.type.descriptor.jdbc.JdbcType;
import org.hibernate.type.descriptor.jdbc.JdbcTypeIndicators;
import org.hibernate.type.descriptor.jdbc.VarcharJdbcType;

public class AccountCodeJavaType extends AbstractClassJavaType<AccountCode> {

    public static final AccountCodeJavaType INSTANCE = new AccountCodeJavaType();

    public AccountCodeJavaType() {

        super(AccountCode.class, ImmutableMutabilityPlan.instance());
    }

    @Override
    public AccountCode fromString(CharSequence string) {

        return (string == null) ? null : new AccountCode(string.toString());
    }

    @Override
    public JdbcType getRecommendedJdbcType(JdbcTypeIndicators context) {

        return VarcharJdbcType.INSTANCE;
    }

    @Override
    public String toString(AccountCode value) {

        return value == null ? null : value.value();
    }

    @Override
    @SuppressWarnings("unchecked")
    public <X> X unwrap(AccountCode value, Class<X> type, WrapperOptions options) {

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
    public AccountCode wrap(Object value, WrapperOptions options) {

        if (value == null) {
            return null;
        }

        if (value instanceof AccountCode code) {
            return code;
        }

        if (value instanceof String s) {
            return new AccountCode(s);
        }

        throw new IllegalArgumentException("Unsupported wrap from " + value.getClass());
    }
}

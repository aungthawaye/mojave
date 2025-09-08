package io.mojaloop.core.common.datatype.converter.type.account;

import io.mojaloop.core.common.datatype.type.account.AccountCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountCodeConverter implements AttributeConverter<AccountCode, String> {

    @Override
    public String convertToDatabaseColumn(AccountCode attribute) {

        return attribute == null ? null : attribute.value();
    }

    @Override
    public AccountCode convertToEntityAttribute(String dbData) {

        return dbData == null ? null : new AccountCode(dbData);
    }
}

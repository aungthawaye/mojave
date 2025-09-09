package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class AccountIdConverter implements AttributeConverter<AccountId, Long> {

    @Override
    public Long convertToDatabaseColumn(AccountId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public AccountId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new AccountId(dbData);
    }

}

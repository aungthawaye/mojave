package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.BalanceHistoryId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class BalanceHistoryIdConverter implements AttributeConverter<BalanceHistoryId, Long> {

    @Override
    public Long convertToDatabaseColumn(BalanceHistoryId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public BalanceHistoryId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new BalanceHistoryId(dbData);
    }

}

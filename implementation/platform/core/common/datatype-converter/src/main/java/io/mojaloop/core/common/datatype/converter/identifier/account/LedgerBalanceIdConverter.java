package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.LedgerBalanceId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LedgerBalanceIdConverter implements AttributeConverter<LedgerBalanceId, Long> {

    @Override
    public Long convertToDatabaseColumn(LedgerBalanceId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public LedgerBalanceId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new LedgerBalanceId(dbData);
    }
}

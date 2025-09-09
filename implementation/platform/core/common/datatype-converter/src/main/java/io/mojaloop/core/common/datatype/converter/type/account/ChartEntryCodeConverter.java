package io.mojaloop.core.common.datatype.converter.type.account;

import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChartEntryCodeConverter implements AttributeConverter<ChartEntryCode, String> {

    @Override
    public String convertToDatabaseColumn(ChartEntryCode attribute) {

        return attribute == null ? null : attribute.value();
    }

    @Override
    public ChartEntryCode convertToEntityAttribute(String dbData) {

        return dbData == null ? null : new ChartEntryCode(dbData);
    }
}

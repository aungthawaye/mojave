package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChartEntryIdConverter implements AttributeConverter<ChartEntryId, Long> {

    @Override
    public Long convertToDatabaseColumn(ChartEntryId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public ChartEntryId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new ChartEntryId(dbData);
    }

}

package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.ChartEntryDefinitionId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChartEntryDefinitionIdConverter implements AttributeConverter<ChartEntryDefinitionId, Long> {

    @Override
    public Long convertToDatabaseColumn(ChartEntryDefinitionId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public ChartEntryDefinitionId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new ChartEntryDefinitionId(dbData);
    }
}

package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class ChartIdConverter implements AttributeConverter<ChartId, Long> {

    @Override
    public Long convertToDatabaseColumn(ChartId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public ChartId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new ChartId(dbData);
    }
}

package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.FspCurrencyId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FspCurrencyIdConverter implements AttributeConverter<FspCurrencyId, Long> {

    @Override
    public Long convertToDatabaseColumn(FspCurrencyId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public FspCurrencyId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new FspCurrencyId(dbData);
    }

}

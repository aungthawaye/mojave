package io.mojaloop.core.participant.domain.component.converter.identifier;

import io.mojaloop.core.common.datatype.identifier.participant.SupportedCurrencyId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class SupportedCurrencyIdConverter implements AttributeConverter<SupportedCurrencyId, Long> {

    @Override
    public Long convertToDatabaseColumn(SupportedCurrencyId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public SupportedCurrencyId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new SupportedCurrencyId(dbData);
    }

}

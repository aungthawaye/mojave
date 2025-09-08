package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.HubCurrencyId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HubCurrencyIdConverter implements AttributeConverter<HubCurrencyId, Long> {

    @Override
    public Long convertToDatabaseColumn(HubCurrencyId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public HubCurrencyId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new HubCurrencyId(dbData);
    }
}

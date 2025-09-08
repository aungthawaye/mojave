package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class HubIdConverter implements AttributeConverter<HubId, Long> {

    @Override
    public Long convertToDatabaseColumn(HubId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public HubId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new HubId(dbData);
    }
}

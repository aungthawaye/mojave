package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.FspEndpointId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FspEndpointIdConverter implements AttributeConverter<FspEndpointId, Long> {

    @Override
    public Long convertToDatabaseColumn(FspEndpointId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public FspEndpointId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new FspEndpointId(dbData);
    }

}

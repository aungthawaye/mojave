package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.EndpointId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class EndpointIdConverter implements AttributeConverter<EndpointId, Long> {

    @Override
    public Long convertToDatabaseColumn(EndpointId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public EndpointId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new EndpointId(dbData);
    }

}

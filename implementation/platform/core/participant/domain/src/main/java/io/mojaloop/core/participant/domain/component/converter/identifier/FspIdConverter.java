package io.mojaloop.core.participant.domain.component.converter.identifier;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FspIdConverter implements AttributeConverter<FspId, Long> {

    @Override
    public Long convertToDatabaseColumn(FspId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public FspId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new FspId(dbData);
    }

}

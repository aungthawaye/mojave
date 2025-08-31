package io.mojaloop.core.common.datatype.converter.identifier.participant;

import io.mojaloop.core.common.datatype.identifier.participant.FxRatePairId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FxRatePairIdConverter implements AttributeConverter<FxRatePairId, Long> {

    @Override
    public Long convertToDatabaseColumn(FxRatePairId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public FxRatePairId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new FxRatePairId(dbData);
    }

}

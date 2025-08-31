package io.mojaloop.core.participant.domain.component.converter.identifier;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OracleIdConverter implements AttributeConverter<OracleId, Long> {

    @Override
    public Long convertToDatabaseColumn(OracleId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public OracleId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new OracleId(dbData);
    }

}

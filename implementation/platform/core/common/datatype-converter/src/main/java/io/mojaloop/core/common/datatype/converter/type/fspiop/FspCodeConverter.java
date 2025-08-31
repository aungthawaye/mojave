package io.mojaloop.core.common.datatype.converter.type.fspiop;

import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class FspCodeConverter implements AttributeConverter<FspCode, String> {

    @Override
    public String convertToDatabaseColumn(FspCode attribute) {

        return attribute == null ? null : attribute.value();
    }

    @Override
    public FspCode convertToEntityAttribute(String dbData) {

        return dbData == null ? null : new FspCode(dbData);
    }

}

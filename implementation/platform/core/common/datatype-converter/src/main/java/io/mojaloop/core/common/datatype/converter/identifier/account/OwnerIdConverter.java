package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class OwnerIdConverter implements AttributeConverter<OwnerId, Long> {

    @Override
    public Long convertToDatabaseColumn(OwnerId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public OwnerId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new OwnerId(dbData);
    }

}

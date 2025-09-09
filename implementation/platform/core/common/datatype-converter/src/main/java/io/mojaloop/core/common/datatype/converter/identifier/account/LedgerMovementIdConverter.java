package io.mojaloop.core.common.datatype.converter.identifier.account;

import io.mojaloop.core.common.datatype.identifier.account.LedgerMovementId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class LedgerMovementIdConverter implements AttributeConverter<LedgerMovementId, Long> {

    @Override
    public Long convertToDatabaseColumn(LedgerMovementId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public LedgerMovementId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new LedgerMovementId(dbData);
    }

}

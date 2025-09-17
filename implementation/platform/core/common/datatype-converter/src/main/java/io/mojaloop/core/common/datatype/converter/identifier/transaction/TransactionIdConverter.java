package io.mojaloop.core.common.datatype.converter.identifier.transaction;

import io.mojaloop.core.common.datatype.identifier.transaction.TransactionId;
import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter(autoApply = true)
public class TransactionIdConverter implements AttributeConverter<TransactionId, Long> {

    @Override
    public Long convertToDatabaseColumn(TransactionId attribute) {

        return attribute == null ? null : attribute.getId();
    }

    @Override
    public TransactionId convertToEntityAttribute(Long dbData) {

        return dbData == null ? null : new TransactionId(dbData);
    }

}

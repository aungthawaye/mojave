package io.mojaloop.common.component.jpa;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

import java.time.Instant;

@Converter
public class JpaInstantConverter implements AttributeConverter<Instant, Long> {

    @Override
    public Long convertToDatabaseColumn(Instant attribute) {

        if (attribute == null) {
            return null;
        }

        return attribute.getEpochSecond();

    }

    @Override
    public Instant convertToEntityAttribute(Long dbData) {

        if (dbData == null) {
            return null;
        }

        return Instant.ofEpochSecond(dbData);

    }

}

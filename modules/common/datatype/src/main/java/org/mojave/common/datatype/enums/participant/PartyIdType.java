package org.mojave.common.datatype.enums.participant;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public enum PartyIdType {

    MSISDN("MSISDN"),

    EMAIL("EMAIL"),

    PERSONAL_ID("PERSONAL_ID"),

    BUSINESS("BUSINESS"),

    DEVICE("DEVICE"),

    ACCOUNT_ID("ACCOUNT_ID"),

    IBAN("IBAN"),

    ALIAS("ALIAS");

    private static final Map<String, PartyIdType> TYPES = new HashMap<>();

    static {

        for (final var b : PartyIdType.values()) {
            TYPES.put(b.value, b);
        }
    }

    private final String value;

    PartyIdType(final String value) {

        this.value = value;
    }

    @JsonCreator
    public static PartyIdType from(final String value) {

        final var b = TYPES.get(value);

        if (b == null) {
            throw new IllegalArgumentException("Unexpected value '" + value + "'");
        }

        return b;
    }

    @Override
    @JsonValue
    public String toString() {

        return String.valueOf(this.value);
    }
}

package io.mojaloop.core.common.datatype.type.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.exception.account.ChartEntryCodeValueRequiredException;
import io.mojaloop.core.common.datatype.exception.account.ChartEntryCodeValueTooLargeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record ChartEntryCode(@JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    public ChartEntryCode {

        if (value == null || value.isBlank()) {
            throw new ChartEntryCodeValueRequiredException();
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new ChartEntryCodeValueTooLargeException();
        }

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ChartEntryCode(String code))) {
            return false;
        }

        return Objects.equals(this.value, code);
    }

}

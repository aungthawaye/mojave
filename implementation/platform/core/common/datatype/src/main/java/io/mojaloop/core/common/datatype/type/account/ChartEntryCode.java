package io.mojaloop.core.common.datatype.type.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record ChartEntryCode(@JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    public ChartEntryCode {

        assert value != null;

        if (value.isBlank()) {
            throw new BlankOrEmptyInputException("Chart Entry Code");
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new TextTooLargeException("Chart Entry Code", StringSizeConstraints.MAX_CODE_LENGTH);
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

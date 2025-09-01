package io.mojaloop.core.common.datatype.type.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record AccountCode(@JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    public AccountCode {

        assert value != null;

        if (value.isBlank()) {
            throw new BlankOrEmptyInputException("Account Code");
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new TextTooLargeException("Account Code", StringSizeConstraints.MAX_CODE_LENGTH);
        }

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof FspCode(String code))) {
            return false;
        }

        return Objects.equals(this.value, code);
    }
}

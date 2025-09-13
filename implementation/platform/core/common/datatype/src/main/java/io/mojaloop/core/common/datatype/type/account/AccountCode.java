package io.mojaloop.core.common.datatype.type.account;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.exception.account.AccountCodeEmptyValueException;
import io.mojaloop.core.common.datatype.exception.account.AccountCodeValueTooLargeException;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.Objects;

public record AccountCode(@JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_CODE_LENGTH) String value) {

    public AccountCode {

        if (value == null || value.isBlank()) {
            throw new AccountCodeEmptyValueException();
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new AccountCodeValueTooLargeException();
        }

    }

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof AccountCode(String code))) {
            return false;
        }

        return Objects.equals(this.value, code);
    }
}

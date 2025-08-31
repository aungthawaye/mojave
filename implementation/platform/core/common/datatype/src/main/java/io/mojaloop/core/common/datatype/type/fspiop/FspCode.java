/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.common.datatype.type.fspiop;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.input.BlankOrEmptyInputException;
import io.mojaloop.component.misc.exception.input.TextTooLargeException;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import java.util.Objects;

public record FspCode(@JsonProperty(required = true) @NotNull @NotBlank String value) {

    public FspCode {

        assert value != null;

        if (value.isBlank()) {
            throw new BlankOrEmptyInputException("FSP Code");
        }

        if (value.length() > StringSizeConstraints.MAX_CODE_LENGTH) {
            throw new TextTooLargeException("FSP Code", StringSizeConstraints.MAX_CODE_LENGTH);
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

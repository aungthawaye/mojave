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

package io.mojaloop.common.datatype.type.fspiop;

import io.mojaloop.common.component.constraint.StringSizeConstraints;
import io.mojaloop.common.component.exception.input.BlankOrEmptyInputException;
import io.mojaloop.common.component.exception.input.TextTooLargeException;
import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Embeddable
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class FspCode {

    @Column(name = "fsp_code", length = StringSizeConstraints.LEN_24)
    private String fspCode;

    public FspCode(String fspCode) {

        assert fspCode != null;

        var value = fspCode.trim();

        if (fspCode.isBlank()) {
            throw new BlankOrEmptyInputException("FSP Code");
        }

        if (value.length() > StringSizeConstraints.LEN_24) {
            throw new TextTooLargeException("FSP Code", StringSizeConstraints.LEN_24);
        }

        this.fspCode = fspCode;
    }

}

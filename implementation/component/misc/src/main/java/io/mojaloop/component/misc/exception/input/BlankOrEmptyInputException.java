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

package io.mojaloop.component.misc.exception.input;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class BlankOrEmptyInputException extends InputException {

    private static final String TEMPLATE = "{0} is required and must not be blank or empty.";

    private final String itemName;

    public BlankOrEmptyInputException(String itemName) {

        super(MessageFormat.format(TEMPLATE, itemName));

        assert itemName != null;

        this.itemName = itemName;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("BLANK_OR_EMPTY_INPUT", TEMPLATE);
    }

}

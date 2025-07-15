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

package io.mojaloop.common.component.exception.input;

import io.mojaloop.common.component.exception.ErrorTemplate;
import io.mojaloop.common.component.exception.InputException;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class WrongInputFormatException extends InputException {

    private static final String TEMPLATE = "{0} is wrong in format. Expected format is {1}.";

    private final String itemName;

    private final String validFormat;

    public WrongInputFormatException(String itemName, String validFormat) {

        super(MessageFormat.format(TEMPLATE, itemName, validFormat));

        assert itemName != null;
        assert validFormat != null;

        this.itemName = itemName;
        this.validFormat = validFormat;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, validFormat};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("WRONG_INPUT_FORMAT", TEMPLATE);
    }

}

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
public class TextTooLargeException extends InputException {

    private static final String TEMPLATE = "The text length of {0} must not exceed {1} characters.";

    private final String itemName;

    private final int maxLength;

    public TextTooLargeException(String itemName, int maxLength) {

        super(MessageFormat.format(TEMPLATE, itemName, maxLength));

        assert itemName != null;
        assert maxLength > 0;

        this.itemName = itemName;
        this.maxLength = maxLength;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, String.valueOf(maxLength)};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("TEXT_TOO_LARGE", TEMPLATE);
    }

}

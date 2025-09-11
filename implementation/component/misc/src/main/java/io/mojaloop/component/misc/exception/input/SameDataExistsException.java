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
public class SameDataExistsException extends InputException {

    private static final String TEMPLATE = "The same {0} exists in {1}.";

    private final String itemName;

    private final String itemContext;

    public SameDataExistsException(String itemName, String itemContext) {

        super(MessageFormat.format(TEMPLATE, itemName, itemContext));

        assert itemName != null;
        assert itemContext != null;

        this.itemName = itemName;
        this.itemContext = itemContext;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, itemContext};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("SAME_DATA_EXISTS", TEMPLATE);
    }

}

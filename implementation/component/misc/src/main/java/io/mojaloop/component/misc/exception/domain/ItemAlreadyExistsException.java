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

package io.mojaloop.component.misc.exception.domain;

import io.mojaloop.component.misc.exception.DomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import lombok.Getter;

import java.text.MessageFormat;

@Getter
public class ItemAlreadyExistsException extends DomainException {

    private final static String TEMPLATE = "{0} ({1}) already exists.";

    private final String itemName;

    private final String itemValue;

    public ItemAlreadyExistsException(String itemName, String itemValue) {

        super(MessageFormat.format(TEMPLATE, itemName, itemValue));

        assert itemName != null;
        assert itemValue != null;

        this.itemName = itemName;
        this.itemValue = itemValue;
    }

    @Override
    public String[] getFillers() {

        return new String[]{itemName, itemValue};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("ITEM_ALREADY_EXISTS", TEMPLATE);
    }

}

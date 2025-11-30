/*-
 * ================================================================================
 * Mojave
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

package io.mojaloop.core.common.datatype.exception.participant;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;

import java.util.Map;

public class FspCodeValueTooLargeException extends UncheckedDomainException {

    public static final String CODE = "FSP_CODE_VALUE_TOO_LARGE";

    private static final String TEMPLATE =
        "FSP Code is too large. Must not exceed " + StringSizeConstraints.MAX_CODE_LENGTH +
            " characters.";

    public FspCodeValueTooLargeException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));
    }

    public static FspCodeValueTooLargeException from(final Map<String, String> extras) {

        return new FspCodeValueTooLargeException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}

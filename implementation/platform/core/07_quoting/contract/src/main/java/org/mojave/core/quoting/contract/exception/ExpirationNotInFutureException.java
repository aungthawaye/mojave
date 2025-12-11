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
package org.mojave.core.quoting.contract.exception;

import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.ErrorTemplate;

import java.util.Map;

public class ExpirationNotInFutureException extends CheckedDomainException {

    public static final String CODE = "EXPIRATION_NOT_IN_FUTURE";

    private static final String TEMPLATE = "Expiration date/time must be in the future.";

    public ExpirationNotInFutureException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));

    }

    public static ExpirationNotInFutureException from(final Map<String, String> extras) {

        return new ExpirationNotInFutureException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}

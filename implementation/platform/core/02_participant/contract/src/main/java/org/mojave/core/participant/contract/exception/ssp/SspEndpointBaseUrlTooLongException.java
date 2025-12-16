/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */
package org.mojave.core.participant.contract.exception.ssp;

import lombok.Getter;
import org.mojave.component.misc.constraint.StringSizeConstraints;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class SspEndpointBaseUrlTooLongException extends UncheckedDomainException {

    public static final String CODE = "SSP_ENDPOINT_BASE_URL_TOO_LONG";

    private static final String TEMPLATE =
        "Base URL of SSP Endpoint is too long. Must not exceed " +
            StringSizeConstraints.MAX_HTTP_URL_LENGTH + " characters.";

    public SspEndpointBaseUrlTooLongException() {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[0]));
    }

    public static SspEndpointBaseUrlTooLongException from(final Map<String, String> extras) {

        return new SspEndpointBaseUrlTooLongException();
    }

    @Override
    public Map<String, String> extras() {

        return Map.of();
    }

}

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

package org.mojave.component.misc.error;

import org.mojave.component.misc.exception.CheckedDomainException;
import org.mojave.component.misc.exception.UncheckedDomainException;
import tools.jackson.databind.ObjectMapper;

import java.util.Map;

public record MojaveErrorResponse(String code, String message, Map<String, String> extras) {

    public static MojaveErrorResponse from(String payload, ObjectMapper objectMapper) {

        try {

            return objectMapper.readValue(payload, MojaveErrorResponse.class);

        } catch (Exception e) {

            throw new IllegalArgumentException("Failed to decode Error Response.", e);
        }
    }

    public static MojaveErrorResponse from(Exception e) {

        if (e instanceof CheckedDomainException ce) {
            return new MojaveErrorResponse(ce.getTemplate().code(), ce.getMessage(), ce.extras());
        }

        if (e instanceof UncheckedDomainException ue) {
            return new MojaveErrorResponse(ue.getTemplate().code(), ue.getMessage(), ue.extras());
        }

        return new MojaveErrorResponse("INTERNAL_SERVER_ERROR", e.getMessage(), Map.of());
    }

}

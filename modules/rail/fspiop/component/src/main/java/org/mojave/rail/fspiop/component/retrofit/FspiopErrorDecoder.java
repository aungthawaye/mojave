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
package org.mojave.rail.fspiop.component.retrofit;

import org.mojave.component.retrofit.RetrofitService;
import org.mojave.scheme.fspiop.core.ErrorInformationResponse;
import tools.jackson.databind.ObjectMapper;
import java.util.Objects;

public class FspiopErrorDecoder implements RetrofitService.ErrorDecoder<ErrorInformationResponse> {

    private final ObjectMapper objectMapper;

    public FspiopErrorDecoder(ObjectMapper objectMapper) {

        Objects.requireNonNull(objectMapper);

        this.objectMapper = objectMapper;
    }

    @Override
    public ErrorInformationResponse decode(int status, String errorResponseBody) {

        return this.objectMapper.readValue(errorResponseBody, ErrorInformationResponse.class);
    }

}

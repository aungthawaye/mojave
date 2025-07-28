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

package io.mojaloop.fspiop.client.component.retrofit;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.fspiop.common.component.FspiopHeaders;
import io.mojaloop.fspiop.common.component.FspiopSignature;
import io.mojaloop.fspiop.common.support.ParticipantDetails;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class FspiopJwsSigningInterceptor implements okhttp3.Interceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger(FspiopJwsSigningInterceptor.class);

    private final ParticipantDetails participantDetails;

    private final ObjectMapper objectMapper;

    public FspiopJwsSigningInterceptor(ParticipantDetails participantDetails, ObjectMapper objectMapper) {

        assert participantDetails != null;
        assert objectMapper != null;

        this.participantDetails = participantDetails;
        this.objectMapper = objectMapper;
    }

    @NotNull
    @Override
    public okhttp3.Response intercept(Chain chain) throws java.io.IOException {

        if (!this.participantDetails.signJws()) {
            return chain.proceed(chain.request());
        }

        var original = chain.request();
        var method = original.method();
        var body = original.body() == null ? "" : original.body().toString();

        if (method.equals("GET") || body.isBlank()) {
            return chain.proceed(chain.request());
        }

        var builder = original.newBuilder();

        var existingHeaders = original.headers();
        var protectedHeaders = new HashMap<String, String>();

        for (var name : existingHeaders.names()) {

            protectedHeaders.put(name, existingHeaders.get(name));
        }

        var signature = this.objectMapper.writeValueAsString(FspiopSignature.sign(this.participantDetails.signingKey(),
                                                                                  protectedHeaders,
                                                                                  body));
        LOGGER.debug("Fspiop signature: [{}]", signature);
        builder.header(FspiopHeaders.Names.FSPIOP_SIGNATURE, signature);

        var modifiedRequest = builder.build();
        LOGGER.debug("Modified request: [{}]", modifiedRequest);

        return chain.proceed(modifiedRequest);
    }

}

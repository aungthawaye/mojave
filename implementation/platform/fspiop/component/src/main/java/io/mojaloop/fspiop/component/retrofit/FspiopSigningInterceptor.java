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

package io.mojaloop.fspiop.component.retrofit;

import tools.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.fspiop.common.participant.ParticipantContext;
import io.mojaloop.fspiop.component.handy.FspiopHeaders;
import io.mojaloop.fspiop.component.handy.FspiopSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class FspiopSigningInterceptor implements okhttp3.Interceptor {

    private final static Logger LOGGER = LoggerFactory.getLogger(FspiopSigningInterceptor.class);

    private final ParticipantContext participantContext;

    private final ObjectMapper objectMapper;

    public FspiopSigningInterceptor(ParticipantContext participantContext,
                                    ObjectMapper objectMapper) {

        assert participantContext != null;
        assert objectMapper != null;

        this.participantContext = participantContext;
        this.objectMapper = objectMapper;
    }

    @Override
    public okhttp3.Response intercept(Chain chain) throws java.io.IOException {

        if (!this.participantContext.signJws()) {
            return chain.proceed(chain.request());
        }

        var startAt = System.nanoTime();

        var original = chain.request();
        String body = null;

        if (original.body() != null) {

            var buffer = new okio.Buffer();

            original.body().writeTo(buffer);

            body = buffer.readUtf8();
        }

        var builder = original.newBuilder();

        var existingHeaders = original.headers();
        var protectedHeaders = new HashMap<String, String>();

        for (var name : existingHeaders.names()) {

            protectedHeaders.put(name, existingHeaders.get(name));
        }

        if (body == null) {
            body = "{\"date\":\"" + protectedHeaders.get(FspiopHeaders.Names.DATE) + "\"}";
        }

        LOGGER.info(
            "Signing protected headers: ({}) and body: ({})", ObjectLogger.log(protectedHeaders),
            ObjectLogger.log(body));

        var genSigStartAt = System.nanoTime();
        var signature = this.objectMapper.writeValueAsString(
            FspiopSignature.sign(this.participantContext.signingKey(), protectedHeaders, body));
        var genSigEndAt = System.nanoTime();
        LOGGER.info("Signature generation took {} ms", (genSigEndAt - genSigStartAt) / 1_000_000);

        builder.header(FspiopHeaders.Names.FSPIOP_SIGNATURE, signature);

        var modifiedRequest = builder.build();

        var endAt = System.nanoTime();
        LOGGER.info("Signing took {} ms", (endAt - startAt) / 1_000_000);

        return chain.proceed(modifiedRequest);
    }

}

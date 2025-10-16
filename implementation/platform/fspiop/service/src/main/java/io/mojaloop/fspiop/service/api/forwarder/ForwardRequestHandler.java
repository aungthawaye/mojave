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

package io.mojaloop.fspiop.service.api.forwarder;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.component.handy.FspiopUrls;
import io.mojaloop.fspiop.component.retrofit.FspiopErrorDecoder;
import io.mojaloop.fspiop.component.retrofit.FspiopInvocationExceptionHandler;
import io.mojaloop.fspiop.service.component.FspiopHttpRequest;
import okhttp3.MediaType;
import okhttp3.RequestBody;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

@Service
public class ForwardRequestHandler implements ForwardRequest {

    private static final Logger LOGGER = LoggerFactory.getLogger(ForwardRequestHandler.class);

    private final RetrofitService.ForwardingService forwardingService;

    private final FspiopErrorDecoder fspiopErrorDecoder;

    private final FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler;

    public ForwardRequestHandler(RetrofitService.ForwardingService forwardingService,
                                 FspiopErrorDecoder fspiopErrorDecoder,
                                 FspiopInvocationExceptionHandler fspiopInvocationExceptionHandler) {

        assert forwardingService != null;
        assert fspiopErrorDecoder != null;
        assert fspiopInvocationExceptionHandler != null;

        this.forwardingService = forwardingService;
        this.fspiopErrorDecoder = fspiopErrorDecoder;
        this.fspiopInvocationExceptionHandler = fspiopInvocationExceptionHandler;
    }

    @Override
    public void forward(String baseUrl, FspiopHttpRequest request) throws FspiopException {

        var method = request.method().toUpperCase();
        var url = FspiopUrls.newUrl(baseUrl, request.uri());

        LOGGER.debug("Forwarding request to : {} {}", method, url);

        try {

            switch (method) {
                case "GET":
                    RetrofitService.invoke(this.forwardingService.get(url, request.headers(), request.params()), this.fspiopErrorDecoder);
                    break;
                case "POST":
                    RetrofitService.invoke(this.forwardingService.post(url,
                                                                       request.headers(),
                                                                       request.params(),
                                                                       RequestBody.create(request.payload(), MediaType.get(request.contentType()))), this.fspiopErrorDecoder);
                    break;
                case "PUT":
                    RetrofitService.invoke(this.forwardingService.put(url,
                                                                      request.headers(),
                                                                      request.params(),
                                                                      RequestBody.create(request.payload(), MediaType.get(request.contentType()))), this.fspiopErrorDecoder);
                    break;
                case "PATCH":
                    RetrofitService.invoke(this.forwardingService.patch(url,
                                                                        request.headers(),
                                                                        request.params(),
                                                                        RequestBody.create(request.payload(), MediaType.get(request.contentType()))), this.fspiopErrorDecoder);
                    break;
                case "DELETE":
                    RetrofitService.invoke(this.forwardingService.delete(url,
                                                                         request.headers(),
                                                                         request.params(),
                                                                         RequestBody.create(request.payload(), MediaType.get(request.contentType()))), this.fspiopErrorDecoder);
                    break;
                default:
                    throw new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);
            }

            LOGGER.debug("Done forwarding request to : {} {}", method, url);

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error forwarding request to : {} {} - error {}", method, baseUrl, e.getMessage());
            throw this.fspiopInvocationExceptionHandler.handle(e);
        }
    }

}

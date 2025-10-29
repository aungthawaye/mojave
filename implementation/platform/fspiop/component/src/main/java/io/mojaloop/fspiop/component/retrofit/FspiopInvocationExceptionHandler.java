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

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopCommunicationException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformationResponse;

import javax.net.ssl.SSLException;
import java.net.SocketException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class FspiopInvocationExceptionHandler {

    public FspiopException handle(RetrofitService.InvocationException exception) {

        var decodedErrorResponse = exception.getDecodedErrorResponse();

        if (decodedErrorResponse != null) {

            // If the decodedErrorResponse is not null, there must be the response from the server.
            if (decodedErrorResponse instanceof ErrorInformationResponse errorInformationResponse) {

                var errorInformation = errorInformationResponse.getErrorInformation();
                var errorDefinition = FspiopErrors.find(errorInformation.getErrorCode());

                if (errorDefinition != null) {

                    // We found the error definition. But we will use the error description returned by the server.
                    return new FspiopException(new ErrorDefinition(errorDefinition.errorType(), errorInformation.getErrorDescription()));
                }

                // We cannot find the error definition. We have no idea what happened at the server.
                return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, errorInformation.getErrorDescription());

            }

            // We cannot parse the error body into ErrorInformationObject. Must be some other JSON object, but we parsed it.
            // We don't know what had happened to the server side or connection issue.
            return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR,
                                       exception.getMessage() == null || exception.getMessage().isBlank() ?
                                           FspiopErrors.GENERIC_SERVER_ERROR.description() :
                                           exception.getMessage());

        }

        var cause = exception.getCause();

        // Something went wrong while sending the request. Then the exception will have the cause.
        if (cause instanceof UnknownHostException || cause instanceof SocketTimeoutException || cause instanceof SocketException || cause instanceof SSLException) {

            return new FspiopCommunicationException(FspiopErrors.DESTINATION_COMMUNICATION_ERROR,
                                                    exception.getMessage() == null || exception.getMessage().isBlank() ?
                                                        FspiopErrors.DESTINATION_COMMUNICATION_ERROR.description() :
                                                        exception.getMessage());
        }

        return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR,
                                   exception.getMessage() == null || exception.getMessage().isBlank() ? FspiopErrors.GENERIC_SERVER_ERROR.description() : exception.getMessage());
    }

}

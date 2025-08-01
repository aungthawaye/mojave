package io.mojaloop.fspiop.component.retrofit;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;
import jakarta.servlet.http.HttpServletResponse;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class FspiopInvocationErrorHandler {

    public FspiopException handle(RetrofitService.InvocationException exception) {

        var cause = exception.getCause();

        if (cause == null) {
            // If the cause is null, there must be the error response.
            var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof ErrorInformationObject errorInformationObject) {

                var errorInformation = errorInformationObject.getErrorInformation();
                var errorDefinition = FspiopErrors.find(errorInformation.getErrorCode());

                if (errorDefinition != null) {

                    return new FspiopException(errorDefinition);
                }

                return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, errorInformation.getErrorDescription());

            }

            // If there is no error response, there must be an error response status code.
            switch (exception.getResponseStatusCode()) {
                case HttpServletResponse.SC_BAD_REQUEST:
                    return new FspiopException(FspiopErrors.MISSING_MANDATORY_ELEMENT);
                case HttpServletResponse.SC_NOT_FOUND:
                    return new FspiopException(FspiopErrors.COMMUNICATION_ERROR);
                case HttpServletResponse.SC_GATEWAY_TIMEOUT:
                case HttpServletResponse.SC_SERVICE_UNAVAILABLE:
                case HttpServletResponse.SC_BAD_GATEWAY:
                    return new FspiopException(FspiopErrors.SERVER_BUSY);
                default:
                    return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);

            }

        }

        // Something went wrong while processing. Then the exception will have the cause.
        if (cause instanceof ConnectException || cause instanceof UnknownHostException || cause instanceof SocketTimeoutException) {

            return new FspiopException(FspiopErrors.COMMUNICATION_ERROR);
        }

        return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, cause.getMessage());
    }

}

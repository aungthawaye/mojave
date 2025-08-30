package io.mojaloop.fspiop.component.retrofit;

import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.fspiop.common.error.ErrorDefinition;
import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

import java.net.ConnectException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;

public class FspiopInvocationErrorHandler {

    public FspiopException handle(RetrofitService.InvocationException exception) {

        var cause = exception.getCause();

        if (cause == null) {

            // If the cause is null, there must be the response from server.
            var decodedErrorResponse = exception.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof ErrorInformationObject errorInformationObject) {

                var errorInformation = errorInformationObject.getErrorInformation();
                var errorDefinition = FspiopErrors.find(errorInformation.getErrorCode());

                if (errorDefinition != null) {

                    // We found the error definition. But we will use the error description returned by the server.
                    return new FspiopException(new ErrorDefinition(errorDefinition.errorType(),
                                                                   errorInformation.getErrorDescription()));
                }

                // We cannot find the error definition. We have no idea what happened at the server.
                return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR, errorInformation.getErrorDescription());

            }

            // We cannot parse the error body into ErrorInformationObject.
            // We don't know what had happened.
            return new FspiopException(FspiopErrors.GENERIC_SERVER_ERROR);

        }

        // Something went wrong while processing. Then the exception will have the cause.
        if (cause instanceof ConnectException || cause instanceof UnknownHostException || cause instanceof SocketTimeoutException) {

            return new FspiopException(FspiopErrors.DESTINATION_COMMUNICATION_ERROR);
        }

        return new FspiopException(FspiopErrors.GENERIC_CLIENT_ERROR, cause.getMessage());
    }

}

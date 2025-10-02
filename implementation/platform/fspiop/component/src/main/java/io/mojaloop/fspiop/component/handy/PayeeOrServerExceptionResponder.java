package io.mojaloop.fspiop.component.handy;

import io.mojaloop.fspiop.common.error.FspiopErrors;
import io.mojaloop.fspiop.common.exception.FspiopCommunicationException;
import io.mojaloop.fspiop.common.exception.FspiopException;
import io.mojaloop.fspiop.common.type.Payer;
import io.mojaloop.fspiop.spec.core.ErrorInformationObject;

public class PayeeOrServerExceptionResponder {

    public static void respond(Payer payer, Exception exception, Action action) throws Throwable {

        if (payer == null || payer.isEmpty()) {

            return;
        }

        ErrorInformationObject errorInformationObject = null;

        if (exception instanceof FspiopCommunicationException fce) {
            errorInformationObject = FspiopErrors.DESTINATION_COMMUNICATION_ERROR.toErrorObject();
        } else if (exception instanceof FspiopException fe) {
            errorInformationObject = fe.toErrorObject();
        } else {
            errorInformationObject = FspiopErrors.GENERIC_SERVER_ERROR.toErrorObject();
            errorInformationObject.getErrorInformation().errorDescription(exception.getMessage());
        }

        action.execute(payer, errorInformationObject);

    }

    public interface Action {

        void execute(Payer payer, ErrorInformationObject errorInformationObject) throws Throwable;

    }

}

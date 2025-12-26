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
package org.mojave.fspiop.component.handy;

import org.mojave.fspiop.component.error.FspiopErrors;
import org.mojave.fspiop.component.exception.FspiopCommunicationException;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payer;
import org.mojave.specification.fspiop.core.ErrorInformationObject;

public class FspiopErrorResponder {

    public static void toPayer(Payer payer, Exception exception, ActionToPayer action)
        throws Exception {

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

    public interface ActionToPayer {

        void execute(Payer payer, ErrorInformationObject errorInformationObject) throws Exception;

    }

}

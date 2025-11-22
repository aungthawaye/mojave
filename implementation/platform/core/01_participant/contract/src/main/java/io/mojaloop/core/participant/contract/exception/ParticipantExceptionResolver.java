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
package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspCurrencyException;
import io.mojaloop.core.participant.contract.exception.fsp.CannotActivateFspEndpointException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeNotFoundException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointBaseUrlRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointBaseUrlTooLongException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameRequiredException;
import io.mojaloop.core.participant.contract.exception.fsp.FspNameTooLongException;
import io.mojaloop.core.participant.contract.exception.hub.HubCountLimitReachedException;
import io.mojaloop.core.participant.contract.exception.hub.HubCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.hub.HubNameRequiredException;
import io.mojaloop.core.participant.contract.exception.hub.HubNameTooLongException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleBaseUrlRequiredException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleBaseUrlTooLongException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleNameRequiredException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleNameTooLongException;
import io.mojaloop.core.participant.contract.exception.oracle.OracleTypeNotFoundException;

public class ParticipantExceptionResolver {

    public static Throwable resolve(RestErrorResponse error) {

        var code = error.code();
        var extra = error.extras();

        return switch (code) {

            // fsp
            case CannotActivateFspCurrencyException.CODE -> CannotActivateFspCurrencyException.from(extra);
            case CannotActivateFspEndpointException.CODE -> CannotActivateFspEndpointException.from(extra);
            case FspCodeAlreadyExistsException.CODE -> FspCodeAlreadyExistsException.from(extra);
            case FspCodeNotFoundException.CODE -> FspCodeNotFoundException.from(extra);
            case FspCodeRequiredException.CODE -> FspCodeRequiredException.from(extra);
            case FspCurrencyAlreadySupportedException.CODE -> FspCurrencyAlreadySupportedException.from(extra);
            case FspCurrencyNotSupportedByHubException.CODE -> FspCurrencyNotSupportedByHubException.from(extra);
            case FspEndpointAlreadyConfiguredException.CODE -> FspEndpointAlreadyConfiguredException.from(extra);
            case FspEndpointBaseUrlRequiredException.CODE -> FspEndpointBaseUrlRequiredException.from(extra);
            case FspEndpointBaseUrlTooLongException.CODE -> FspEndpointBaseUrlTooLongException.from(extra);
            case FspIdNotFoundException.CODE -> FspIdNotFoundException.from(extra);
            case FspNameRequiredException.CODE -> FspNameRequiredException.from(extra);
            case FspNameTooLongException.CODE -> FspNameTooLongException.from(extra);

            // hub
            case HubCountLimitReachedException.CODE -> HubCountLimitReachedException.from(extra);
            case HubCurrencyAlreadySupportedException.CODE -> HubCurrencyAlreadySupportedException.from(extra);
            case HubNameRequiredException.CODE -> HubNameRequiredException.from(extra);
            case HubNameTooLongException.CODE -> HubNameTooLongException.from(extra);
            case HubNotFoundException.CODE -> HubNotFoundException.from(extra);

            // oracle
            case OracleAlreadyExistsException.CODE -> OracleAlreadyExistsException.from(extra);
            case OracleBaseUrlRequiredException.CODE -> OracleBaseUrlRequiredException.from(extra);
            case OracleBaseUrlTooLongException.CODE -> OracleBaseUrlTooLongException.from(extra);
            case OracleIdNotFoundException.CODE -> OracleIdNotFoundException.from(extra);
            case OracleNameRequiredException.CODE -> OracleNameRequiredException.from(extra);
            case OracleNameTooLongException.CODE -> OracleNameTooLongException.from(extra);
            case OracleTypeNotFoundException.CODE -> OracleTypeNotFoundException.from(extra);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}

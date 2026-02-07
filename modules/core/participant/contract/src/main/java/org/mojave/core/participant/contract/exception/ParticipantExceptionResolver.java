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

package org.mojave.core.participant.contract.exception;

import org.mojave.component.misc.error.RestErrorResponse;
import org.mojave.core.participant.contract.exception.fsp.CannotActivateFspCurrencyException;
import org.mojave.core.participant.contract.exception.fsp.CannotActivateFspEndpointException;
import org.mojave.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import org.mojave.core.participant.contract.exception.fsp.FspCodeNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspCodeRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import org.mojave.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import org.mojave.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import org.mojave.core.participant.contract.exception.fsp.FspEndpointBaseUrlRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspEndpointBaseUrlTooLongException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameAlreadyExistsException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspGroupNameTooLongException;
import org.mojave.core.participant.contract.exception.fsp.FspIdNotFoundException;
import org.mojave.core.participant.contract.exception.fsp.FspNameRequiredException;
import org.mojave.core.participant.contract.exception.fsp.FspNameTooLongException;
import org.mojave.core.participant.contract.exception.fsp.TerminatedFspIdException;
import org.mojave.core.participant.contract.exception.hub.HubCountLimitReachedException;
import org.mojave.core.participant.contract.exception.hub.HubCurrencyAlreadySupportedException;
import org.mojave.core.participant.contract.exception.hub.HubNameRequiredException;
import org.mojave.core.participant.contract.exception.hub.HubNameTooLongException;
import org.mojave.core.participant.contract.exception.hub.HubNotFoundException;
import org.mojave.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
import org.mojave.core.participant.contract.exception.oracle.OracleBaseUrlRequiredException;
import org.mojave.core.participant.contract.exception.oracle.OracleBaseUrlTooLongException;
import org.mojave.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import org.mojave.core.participant.contract.exception.oracle.OracleNameRequiredException;
import org.mojave.core.participant.contract.exception.oracle.OracleNameTooLongException;
import org.mojave.core.participant.contract.exception.oracle.OracleTypeNotFoundException;
import org.mojave.core.participant.contract.exception.ssp.SspCodeRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspCurrencyNotSupportedByHubException;
import org.mojave.core.participant.contract.exception.ssp.SspEndpointBaseUrlRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspEndpointBaseUrlTooLongException;
import org.mojave.core.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.core.participant.contract.exception.ssp.SspNameRequiredException;
import org.mojave.core.participant.contract.exception.ssp.SspNameTooLongException;

public class ParticipantExceptionResolver {

    public static Throwable resolve(RestErrorResponse error) {

        var code = error.code();
        var extra = error.extras();

        return switch (code) {

            // fsp
            case CannotActivateFspCurrencyException.CODE ->
                CannotActivateFspCurrencyException.from(extra);
            case CannotActivateFspEndpointException.CODE ->
                CannotActivateFspEndpointException.from(extra);
            case FspCodeAlreadyExistsException.CODE -> FspCodeAlreadyExistsException.from(extra);
            case FspCodeNotFoundException.CODE -> FspCodeNotFoundException.from(extra);
            case FspCodeRequiredException.CODE -> FspCodeRequiredException.from(extra);
            case FspCurrencyAlreadySupportedException.CODE ->
                FspCurrencyAlreadySupportedException.from(extra);
            case FspCurrencyNotSupportedByHubException.CODE ->
                FspCurrencyNotSupportedByHubException.from(extra);
            case FspEndpointAlreadyConfiguredException.CODE ->
                FspEndpointAlreadyConfiguredException.from(extra);
            case FspEndpointBaseUrlRequiredException.CODE ->
                FspEndpointBaseUrlRequiredException.from(extra);
            case FspEndpointBaseUrlTooLongException.CODE ->
                FspEndpointBaseUrlTooLongException.from(extra);
            case FspIdNotFoundException.CODE -> FspIdNotFoundException.from(extra);
            case FspNameRequiredException.CODE -> FspNameRequiredException.from(extra);
            case FspNameTooLongException.CODE -> FspNameTooLongException.from(extra);
            case TerminatedFspIdException.CODE -> TerminatedFspIdException.from(extra);

            // fsp group
            case FspGroupNameAlreadyExistsException.CODE ->
                FspGroupNameAlreadyExistsException.from(extra);
            case FspGroupNameRequiredException.CODE -> FspGroupNameRequiredException.from(extra);
            case FspGroupNameTooLongException.CODE -> FspGroupNameTooLongException.from(extra);

            // hub
            case HubCountLimitReachedException.CODE -> HubCountLimitReachedException.from(extra);
            case HubCurrencyAlreadySupportedException.CODE ->
                HubCurrencyAlreadySupportedException.from(extra);
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

            // ssp
            case SspEndpointBaseUrlRequiredException.CODE ->
                SspEndpointBaseUrlRequiredException.from(extra);
            case SspEndpointBaseUrlTooLongException.CODE ->
                SspEndpointBaseUrlTooLongException.from(extra);
            case SspIdNotFoundException.CODE -> SspIdNotFoundException.from(extra);
            case SspNameRequiredException.CODE -> SspNameRequiredException.from(extra);
            case SspNameTooLongException.CODE -> SspNameTooLongException.from(extra);
            case SspCodeRequiredException.CODE -> SspCodeRequiredException.from(extra);
            case SspCurrencyNotSupportedByHubException.CODE ->
                SspCurrencyNotSupportedByHubException.from(extra);

            default -> throw new RuntimeException("Unknown exception code: " + code);
        };
    }

}

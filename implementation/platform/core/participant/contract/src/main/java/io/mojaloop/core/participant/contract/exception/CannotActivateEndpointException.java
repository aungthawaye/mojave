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

package io.mojaloop.core.participant.contract.exception;

import io.mojaloop.component.misc.exception.DomainException;
import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;

public class CannotActivateEndpointException extends DomainException {

    private static final String TEMPLATE = "Cannot activate the endpoint, {0}. FSP is not active.";

    public CannotActivateEndpointException(EndpointType endpointType) {

        super(new ErrorTemplate("CANNOT_ACTIVE_ENDPOINT", TEMPLATE), endpointType.name());

    }

}

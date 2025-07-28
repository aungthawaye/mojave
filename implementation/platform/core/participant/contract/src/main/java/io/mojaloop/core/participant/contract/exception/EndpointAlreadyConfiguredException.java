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

import io.mojaloop.common.component.exception.DomainException;
import io.mojaloop.common.component.exception.ErrorTemplate;

import java.text.MessageFormat;

public class EndpointAlreadyConfiguredException extends DomainException {

    private static final String TEMPLATE = "Endpoint type ({0}) is already configured.";

    private final String endpointType;

    public EndpointAlreadyConfiguredException(String endpointType) {

        super(MessageFormat.format(TEMPLATE, endpointType));

        assert endpointType != null;

        this.endpointType = endpointType;
    }

    @Override
    public String[] getFillers() {

        return new String[]{endpointType};
    }

    @Override
    public ErrorTemplate getTemplate() {

        return new ErrorTemplate("ENDPOINT_ALREADY_CONFIGURED", TEMPLATE);
    }

}

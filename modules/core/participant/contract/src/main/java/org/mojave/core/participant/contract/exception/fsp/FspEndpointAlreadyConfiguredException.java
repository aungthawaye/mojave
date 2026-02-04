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
package org.mojave.core.participant.contract.exception.fsp;

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.common.datatype.enums.participant.EndpointType;

import java.util.HashMap;
import java.util.Map;

@Getter
public class FspEndpointAlreadyConfiguredException extends UncheckedDomainException {

    public static final String CODE = "FSP_ENDPOINT_ALREADY_CONFIGURED";

    private static final String TEMPLATE = "Endpoint type ({0}) is already configured.";

    private final EndpointType endpointType;

    public FspEndpointAlreadyConfiguredException(final EndpointType endpointType) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{endpointType.name()}));

        this.endpointType = endpointType;

    }

    public static FspEndpointAlreadyConfiguredException from(final Map<String, String> extras) {

        final var type = EndpointType.valueOf(extras.get(Keys.ENDPOINT_TYPE));

        return new FspEndpointAlreadyConfiguredException(type);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ENDPOINT_TYPE, this.endpointType.name());

        return extras;
    }

    public static class Keys {

        public static final String ENDPOINT_TYPE = "endpointType";

    }

}

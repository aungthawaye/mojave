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
import org.mojave.core.common.datatype.enums.fspiop.EndpointType;
import org.mojave.core.common.datatype.type.participant.FspCode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CannotActivateFspEndpointException extends UncheckedDomainException {

    public static final String CODE = "CANNOT_ACTIVATE_FSP_ENDPOINT";

    private static final String TEMPLATE = "Cannot activate the endpoint, {0}. FSP ({1}) is not active.";

    private final FspCode fspCode;

    private final EndpointType endpointType;

    public CannotActivateFspEndpointException(FspCode fspCode, EndpointType endpointType) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            endpointType.name(),
            fspCode.value()}));

        this.fspCode = fspCode;
        this.endpointType = endpointType;

    }

    public static CannotActivateFspEndpointException from(final Map<String, String> extras) {

        final var endpointType = EndpointType.valueOf(extras.get(Keys.ENDPOINT_TYPE));
        final var fspCode = new FspCode(extras.get(Keys.FSP_CODE));

        return new CannotActivateFspEndpointException(fspCode, endpointType);
    }

    @Override
    public Map<String, String> extras() {

        var extras = new HashMap<String, String>();

        extras.put(Keys.FSP_CODE, fspCode.value());
        extras.put(Keys.ENDPOINT_TYPE, endpointType.name());

        return extras;
    }

    public static class Keys {

        public static final String ENDPOINT_TYPE = "endpointType";

        public static final String FSP_CODE = "code";

    }

}

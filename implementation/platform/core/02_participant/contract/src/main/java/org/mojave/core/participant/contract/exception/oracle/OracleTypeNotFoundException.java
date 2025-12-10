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

package org.mojave.core.participant.contract.exception.oracle;

import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.fspiop.spec.core.PartyIdType;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OracleTypeNotFoundException extends UncheckedDomainException {

    public static final String CODE = "ORACLE_TYPE_NOT_FOUND";

    private static final String TEMPLATE = "Oracle Type ({0}) cannot be not found.";

    private final PartyIdType type;

    public OracleTypeNotFoundException(final PartyIdType type) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{type.name()}));

        this.type = type;

    }

    public static OracleTypeNotFoundException from(final Map<String, String> extras) {

        final var type = PartyIdType.valueOf(extras.get(Keys.PARTY_ID_TYPE));

        return new OracleTypeNotFoundException(type);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.PARTY_ID_TYPE, this.type.name());

        return extras;
    }

    public static class Keys {

        public static final String PARTY_ID_TYPE = "partyIdType";

    }

}

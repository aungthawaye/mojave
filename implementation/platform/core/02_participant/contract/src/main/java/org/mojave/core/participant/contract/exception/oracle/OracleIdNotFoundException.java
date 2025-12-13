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

import lombok.Getter;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.core.common.datatype.identifier.participant.OracleId;

import java.util.HashMap;
import java.util.Map;

@Getter
public class OracleIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "ORACLE_ID_NOT_FOUND";

    private static final String TEMPLATE = "Oracle ID ({0}) cannot be not found.";

    private final OracleId oracleId;

    public OracleIdNotFoundException(final OracleId oracleId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{oracleId.getId().toString()}));

        this.oracleId = oracleId;

    }

    public static OracleIdNotFoundException from(final Map<String, String> extras) {

        final var oracleId = new OracleId(Long.valueOf(extras.get(Keys.ORACLE_ID)));

        return new OracleIdNotFoundException(oracleId);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.ORACLE_ID, this.oracleId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String ORACLE_ID = "oracleId";

    }

}

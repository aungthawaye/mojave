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

package org.mojave.core.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.scheme.rule.type.accounting.CoaEntryCode;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CoaEntryConflictInDefinitionException extends UncheckedDomainException {

    public static final String CODE = "COA_ENTRY_CONFLICT_IN_DEFINITION";

    private static final String TEMPLATE = "COA Entry ({0}) conflicts in Definition.";

    private final CoaEntryCode coaEntryCode;

    public CoaEntryConflictInDefinitionException(final CoaEntryCode coaEntryCode) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{coaEntryCode.value()}));

        this.coaEntryCode = coaEntryCode;
    }

    public static CoaEntryConflictInDefinitionException from(final Map<String, String> extras) {

        final var code = new CoaEntryCode(extras.get(Keys.COA_ENTRY_CODE));

        return new CoaEntryConflictInDefinitionException(code);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.COA_ENTRY_CODE, this.coaEntryCode.value());

        return extras;
    }

    public static class Keys {

        public static final String COA_ENTRY_CODE = "coaEntryCode";

    }

}

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

package org.mojave.core.accounting.contract.exception.chart;

import lombok.Getter;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.HashMap;
import java.util.Map;

@Getter
public class CoaEntryIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "COA_ENTRY_ID_NOT_FOUND";

    private static final String TEMPLATE = "COA Entry ID ({0}) cannot be found.";

    private final CoaEntryId coaEntryId;

    public CoaEntryIdNotFoundException(final CoaEntryId coaEntryId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{coaEntryId.getId().toString()}));

        this.coaEntryId = coaEntryId;
    }

    public static CoaEntryIdNotFoundException from(final Map<String, String> extras) {

        final var id = new CoaEntryId(Long.valueOf(extras.get(Keys.COA_ENTRY_ID)));

        return new CoaEntryIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.COA_ENTRY_ID, this.coaEntryId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String COA_ENTRY_ID = "coaEntryId";

    }

}

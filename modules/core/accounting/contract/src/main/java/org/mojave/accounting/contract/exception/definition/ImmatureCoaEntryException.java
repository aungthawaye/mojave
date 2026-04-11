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

package org.mojave.accounting.contract.exception.definition;

import lombok.Getter;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;

import java.util.Map;

@Getter
public class ImmatureCoaEntryException extends UncheckedDomainException {

    public static final String CODE = "IMMATURE_COA_ENTRY";

    private static final String TEMPLATE = "COA Entry ({0}) is immature. No Accounts have been configured for it.";

    private final CoaEntryCode coaEntryCode;

    public ImmatureCoaEntryException(CoaEntryCode coaEntryCode) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{coaEntryCode.value()}));

        this.coaEntryCode = coaEntryCode;
    }

    public static ImmatureCoaEntryException from(final Map<String, String> extras) {

        return new ImmatureCoaEntryException(
            new CoaEntryCode(extras.get(Keys.COA_ENTRY_CODE)));
    }

    @Override
    public Map<String, String> extras() {

        return Map.of(Keys.COA_ENTRY_CODE, coaEntryCode.value());
    }

    public static class Keys {

        public static final String COA_ENTRY_CODE = "coaEntryCode";

    }

}

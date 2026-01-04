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
import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import org.mojave.scheme.common.datatype.type.accounting.ChartEntryCode;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ChartEntryCodeAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "CHART_ENTRY_CODE_ALREADY_EXISTS";

    private static final String TEMPLATE = "The Chart Entry Code ({0}) already exists.";

    private final ChartEntryCode code;

    public ChartEntryCodeAlreadyExistsException(final ChartEntryCode code) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{code.value()}));

        this.code = code;
    }

    public static ChartEntryCodeAlreadyExistsException from(final Map<String, String> extras) {

        final var code = new ChartEntryCode(extras.get(Keys.CHART_ENTRY_CODE));

        return new ChartEntryCodeAlreadyExistsException(code);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.CHART_ENTRY_CODE, this.code.value());

        return extras;
    }

    public static class Keys {

        public static final String CHART_ENTRY_CODE = "chartEntryCode";

    }

}

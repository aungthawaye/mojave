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
/*-
 * ==============================================================================
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
 * ==============================================================================
 */

package org.mojave.core.accounting.contract.exception.chart;

import org.mojave.component.misc.exception.ErrorTemplate;
import org.mojave.component.misc.exception.UncheckedDomainException;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ChartEntryNameAlreadyExistsException extends UncheckedDomainException {

    public static final String CODE = "CHART_ENTRY_NAME_ALREADY_EXISTS";

    private static final String TEMPLATE = "The Chart Entry Name ({0}) already exists in Chart ({1}).";

    private final String chartEntryName;

    private final String chartName;

    public ChartEntryNameAlreadyExistsException(final String chartEntryName,
                                                final String chartName) {

        super(new ErrorTemplate(
            CODE, TEMPLATE, new String[]{
            chartEntryName,
            chartName}));

        this.chartEntryName = chartEntryName;
        this.chartName = chartName;
    }

    public static ChartEntryNameAlreadyExistsException from(final Map<String, String> extras) {

        final var entryName = extras.get(Keys.CHART_ENTRY_NAME);
        final var name = extras.get(Keys.CHART_NAME);

        return new ChartEntryNameAlreadyExistsException(entryName, name);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.CHART_ENTRY_NAME, this.chartEntryName);
        extras.put(Keys.CHART_NAME, this.chartName);

        return extras;
    }

    public static class Keys {

        public static final String CHART_ENTRY_NAME = "chartEntryName";

        public static final String CHART_NAME = "chartName";

    }

}

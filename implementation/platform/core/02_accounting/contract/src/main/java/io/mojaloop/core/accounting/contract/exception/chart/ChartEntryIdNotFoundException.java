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

package io.mojaloop.core.accounting.contract.exception.chart;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.UncheckedDomainException;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import lombok.Getter;

import java.util.HashMap;
import java.util.Map;

@Getter
public class ChartEntryIdNotFoundException extends UncheckedDomainException {

    public static final String CODE = "CHART_ENTRY_ID_NOT_FOUND";

    private static final String TEMPLATE = "Chart Entry ID ({0}) cannot be not found.";

    private final ChartEntryId chartEntryId;

    public ChartEntryIdNotFoundException(final ChartEntryId chartEntryId) {

        super(new ErrorTemplate(CODE, TEMPLATE, new String[]{chartEntryId.getId().toString()}));

        this.chartEntryId = chartEntryId;
    }

    public static ChartEntryIdNotFoundException from(final Map<String, String> extras) {

        final var id = new ChartEntryId(Long.valueOf(extras.get(Keys.CHART_ENTRY_ID)));

        return new ChartEntryIdNotFoundException(id);
    }

    @Override
    public Map<String, String> extras() {

        final var extras = new HashMap<String, String>();

        extras.put(Keys.CHART_ENTRY_ID, this.chartEntryId.getId().toString());

        return extras;
    }

    public static class Keys {

        public static final String CHART_ENTRY_ID = "chartEntryId";

    }

}

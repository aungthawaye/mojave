/*-
 * ==============================================================================
 * Mojaloop OSS
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

package io.mojaloop.core.account.contract.exception.account;

import io.mojaloop.component.misc.exception.ErrorTemplate;
import io.mojaloop.component.misc.exception.InputException;
import io.mojaloop.core.common.datatype.enums.account.ChartType;

public class RequireFspChartTypeException extends InputException {

    private static final String TEMPLATE = "Chart type must be FSP to create account for FSP. Now it is {0}.";

    public RequireFspChartTypeException(ChartType chartType) {

        super(new ErrorTemplate("REQUIRE_FSP_CHART_TYPE", TEMPLATE), chartType.toString());
    }

}

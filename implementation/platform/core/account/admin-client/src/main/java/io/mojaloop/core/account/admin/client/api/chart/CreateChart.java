/*-
 * ================================================================================
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
 * ================================================================================
 */
/*-
 * ==============================================================================
 * Mojaloop OSS
 * -----------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * -----------------------------------------------------------------------------
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

package io.mojaloop.core.account.admin.client.api.chart;

import com.fasterxml.jackson.databind.ObjectMapper;
import io.mojaloop.component.misc.error.RestErrorResponse;
import io.mojaloop.component.retrofit.RetrofitService;
import io.mojaloop.core.account.admin.client.exception.AccountAdminClientException;
import io.mojaloop.core.account.admin.client.service.AccountAdminService;
import io.mojaloop.core.account.contract.command.chart.CreateChartCommand;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

@Component
public class CreateChart {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateChart.class);

    private final AccountAdminService.ChartCommands chartCommands;

    private final ObjectMapper objectMapper;

    public CreateChart(AccountAdminService.ChartCommands chartCommands, ObjectMapper objectMapper) {

        assert chartCommands != null;
        assert objectMapper != null;

        this.chartCommands = chartCommands;
        this.objectMapper = objectMapper;
    }

    public CreateChartCommand.Output execute(CreateChartCommand.Input input) throws AccountAdminClientException {

        try {

            return RetrofitService
                       .invoke(this.chartCommands.createChart(input),
                               (status, errorResponseBody) -> RestErrorResponse.decode(errorResponseBody, this.objectMapper))
                       .body();

        } catch (RetrofitService.InvocationException e) {

            LOGGER.error("Error invoking createChart : {}", e.getMessage());

            var decodedErrorResponse = e.getDecodedErrorResponse();

            if (decodedErrorResponse instanceof RestErrorResponse(String code, String message)) {

                throw new AccountAdminClientException(code, message);
            }

            throw new AccountAdminClientException("INTERNAL_SERVER_ERROR", e.getMessage());
        }
    }

}

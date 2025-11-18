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

package io.mojaloop.core.participant.admin.controller.command.oracle;

import io.mojaloop.core.participant.contract.command.oracle.DeactivateOracleCommand;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeactivateOracleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateOracleController.class);

    private final DeactivateOracleCommand deactivateOracleCommand;

    public DeactivateOracleController(DeactivateOracleCommand deactivateOracleCommand) {

        assert deactivateOracleCommand != null;

        this.deactivateOracleCommand = deactivateOracleCommand;
    }

    @PostMapping("/oracles/deactivate-oracle")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public DeactivateOracleCommand.Output execute(@Valid @RequestBody DeactivateOracleCommand.Input input) {

        return this.deactivateOracleCommand.execute(input);
    }

}

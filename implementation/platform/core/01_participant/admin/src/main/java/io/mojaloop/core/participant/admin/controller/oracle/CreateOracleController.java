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

package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.command.oracle.CreateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleAlreadyExistsException;
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
public class CreateOracleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateOracleController.class.getName());

    private final CreateOracleCommand createOracleCommand;

    public CreateOracleController(CreateOracleCommand createOracleCommand) {

        assert createOracleCommand != null;

        this.createOracleCommand = createOracleCommand;
    }

    @PostMapping("/oracles/create-oracle")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateOracleCommand.Output execute(@Valid @RequestBody CreateOracleCommand.Input input) {

        return this.createOracleCommand.execute(input);
    }

}

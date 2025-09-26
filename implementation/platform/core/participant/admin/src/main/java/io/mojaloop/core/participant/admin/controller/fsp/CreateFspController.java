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

package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCodeAlreadyExistsException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspEndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
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
public class CreateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFspController.class.getName());

    private final CreateFspCommand createFspCommand;

    public CreateFspController(CreateFspCommand createFspCommand) {

        assert createFspCommand != null;

        this.createFspCommand = createFspCommand;
    }

    @PostMapping("/fsps/create-fsp")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public CreateFspCommand.Output execute(@Valid @RequestBody CreateFspCommand.Input input) throws
                                                                                             FspCodeAlreadyExistsException,
                                                                                             FspEndpointAlreadyConfiguredException,
                                                                                             FspCurrencyAlreadySupportedException,
                                                                                             FspCurrencyNotSupportedByHubException,
                                                                                             HubNotFoundException {

        return this.createFspCommand.execute(input);
    }

}

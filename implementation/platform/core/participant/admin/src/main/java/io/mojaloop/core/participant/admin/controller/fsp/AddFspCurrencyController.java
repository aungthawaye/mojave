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

import io.mojaloop.core.participant.contract.command.fsp.AddFspCurrencyCommand;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.fsp.FspCurrencyNotSupportedByHubException;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
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
public class AddFspCurrencyController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFspCurrencyController.class);

    private final AddFspCurrencyCommand addFspCurrencyCommand;

    public AddFspCurrencyController(AddFspCurrencyCommand addFspCurrencyCommand) {

        assert addFspCurrencyCommand != null;

        this.addFspCurrencyCommand = addFspCurrencyCommand;
    }

    @PostMapping("/fsps/add-currency")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AddFspCurrencyCommand.Output execute(@Valid @RequestBody AddFspCurrencyCommand.Input input)
        throws FspIdNotFoundException, FspCurrencyAlreadySupportedException, FspCurrencyNotSupportedByHubException, HubNotFoundException {

        return this.addFspCurrencyCommand.execute(input);
    }

}

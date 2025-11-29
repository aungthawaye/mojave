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
package io.mojaloop.core.wallet.intercom.controller.api.command.position;

import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FulfilPositionsController {

    private final FulfilPositionsCommand fulfilPositionsCommand;

    public FulfilPositionsController(final FulfilPositionsCommand fulfilPositionsCommand) {

        assert fulfilPositionsCommand != null;

        this.fulfilPositionsCommand = fulfilPositionsCommand;
    }

    @PostMapping("/positions/fulfil")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FulfilPositionsCommand.Output execute(@Valid @RequestBody final FulfilPositionsCommand.Input input)
        throws
        FailedToCommitReservationException,
        NoPositionUpdateForTransactionException,
        PositionNotExistException {

        return this.fulfilPositionsCommand.execute(input);
    }

}

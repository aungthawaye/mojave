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
package org.mojave.core.wallet.admin.controller.api.command.position;

import jakarta.validation.Valid;
import org.mojave.core.wallet.contract.command.position.ReservePositionCommand;
import org.mojave.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import org.mojave.core.wallet.contract.exception.position.PositionLimitExceededException;
import org.mojave.core.wallet.contract.exception.position.PositionNotExistException;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.Objects;

@RestController
@RequestMapping("/wallet")
public class ReservePositionController {

    private final ReservePositionCommand reservePositionCommand;

    public ReservePositionController(final ReservePositionCommand reservePositionCommand) {

        Objects.requireNonNull(reservePositionCommand);

        this.reservePositionCommand = reservePositionCommand;
    }

    @PostMapping("/positions/reserve")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ReservePositionCommand.Output execute(
        @Valid @RequestBody final ReservePositionCommand.Input input) throws
                                                                      NoPositionUpdateForTransactionException,
                                                                      PositionLimitExceededException,
                                                                      PositionNotExistException {

        return this.reservePositionCommand.execute(input);
    }

}

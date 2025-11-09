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
package io.mojaloop.core.wallet.intercom.controller;

import io.mojaloop.core.wallet.contract.command.position.CommitPositionCommand;
import io.mojaloop.core.wallet.contract.command.position.DecreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.IncreasePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.ReservePositionCommand;
import io.mojaloop.core.wallet.contract.command.position.RollbackPositionCommand;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionLimitExceededException;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import jakarta.validation.Valid;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class PositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(PositionController.class);

    private final IncreasePositionCommand increasePositionCommand;

    private final DecreasePositionCommand decreasePositionCommand;

    private final ReservePositionCommand reservePositionCommand;

    private final RollbackPositionCommand rollbackPositionCommand;

    private final CommitPositionCommand commitPositionCommand;

    private final PositionQuery positionQuery;

    public PositionController(IncreasePositionCommand increasePositionCommand,
                              DecreasePositionCommand decreasePositionCommand,
                              ReservePositionCommand reservePositionCommand,
                              RollbackPositionCommand rollbackPositionCommand,
                              CommitPositionCommand commitPositionCommand,
                              PositionQuery positionQuery) {

        assert increasePositionCommand != null;
        assert decreasePositionCommand != null;
        assert reservePositionCommand != null;
        assert rollbackPositionCommand != null;
        assert commitPositionCommand != null;
        assert positionQuery != null;

        this.increasePositionCommand = increasePositionCommand;
        this.decreasePositionCommand = decreasePositionCommand;
        this.reservePositionCommand = reservePositionCommand;
        this.rollbackPositionCommand = rollbackPositionCommand;
        this.commitPositionCommand = commitPositionCommand;
        this.positionQuery = positionQuery;
    }

    @GetMapping("/positions")
    public List<PositionData> execute() {

        return this.positionQuery.getAll();
    }

    @PostMapping("/positions/increase")
    public IncreasePositionCommand.Output execute(@Valid @RequestBody IncreasePositionCommand.Input input)
        throws NoPositionUpdateForTransactionException, PositionLimitExceededException {

        LOGGER.info("Entering IncreasePositionCommand.execute: input : {}", input);

        final var output = this.increasePositionCommand.execute(input);

        LOGGER.info("Exiting IncreasePositionCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/positions/decrease")
    public DecreasePositionCommand.Output execute(@Valid @RequestBody DecreasePositionCommand.Input input) {

        LOGGER.info("Entering DecreasePositionCommand.execute: input : {}", input);

        final var output = this.decreasePositionCommand.execute(input);

        LOGGER.info("Exiting DecreasePositionCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/positions/reserve")
    public ReservePositionCommand.Output execute(@Valid @RequestBody ReservePositionCommand.Input input) {

        LOGGER.info("Entering ReservePositionCommand.execute: input : {}", input);

        final var output = this.reservePositionCommand.execute(input);

        LOGGER.info("Exiting ReservePositionCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/positions/rollback")
    public RollbackPositionCommand.Output execute(@Valid @RequestBody RollbackPositionCommand.Input input) {

        LOGGER.info("Entering RollbackPositionCommand.execute: input : {}", input);

        final var output = this.rollbackPositionCommand.execute(input);

        LOGGER.info("Exiting RollbackPositionCommand.execute: {}", output);

        return output;
    }

    @PostMapping("/positions/commit")
    public CommitPositionCommand.Output execute(@Valid @RequestBody CommitPositionCommand.Input input) {

        LOGGER.info("Entering CommitPositionCommand.execute: input : {}", input);

        final var output = this.commitPositionCommand.execute(input);

        LOGGER.info("Exiting CommitPositionCommand.execute: {}", output);

        return output;
    }

}

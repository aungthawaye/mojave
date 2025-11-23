package io.mojaloop.core.wallet.intercom.controller.api.command.position;

import io.mojaloop.core.wallet.contract.command.position.FulfilPositionsCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToCommitReservationException;
import io.mojaloop.core.wallet.contract.exception.position.NoPositionUpdateForTransactionException;
import io.mojaloop.core.wallet.contract.exception.position.PositionNotExistException;
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
public class FulfilPositionsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FulfilPositionsController.class);

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

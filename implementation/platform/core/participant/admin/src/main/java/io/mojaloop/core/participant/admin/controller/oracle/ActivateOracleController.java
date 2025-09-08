package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.command.oracle.ActivateOracleCommand;
import io.mojaloop.core.participant.contract.exception.OracleIdNotFoundException;
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
public class ActivateOracleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateOracleController.class);

    private final ActivateOracleCommand activateOracleCommand;

    public ActivateOracleController(ActivateOracleCommand activateOracleCommand) {
        assert activateOracleCommand != null;
        this.activateOracleCommand = activateOracleCommand;
    }

    @PostMapping("/oracles/activate-oracle")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ActivateOracleCommand.Output execute(@Valid @RequestBody ActivateOracleCommand.Input input)
        throws OracleIdNotFoundException {
        return this.activateOracleCommand.execute(input);
    }
}

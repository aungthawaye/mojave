package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.command.oracle.DeactivateOracleCommand;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
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
    public DeactivateOracleCommand.Output execute(@Valid @RequestBody DeactivateOracleCommand.Input input)
        throws OracleIdNotFoundException {
        return this.deactivateOracleCommand.execute(input);
    }
}

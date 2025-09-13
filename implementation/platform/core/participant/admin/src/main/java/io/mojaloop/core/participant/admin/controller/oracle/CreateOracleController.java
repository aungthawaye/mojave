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
    public CreateOracleCommand.Output execute(@Valid @RequestBody CreateOracleCommand.Input input)
        throws OracleAlreadyExistsException {

        return this.createOracleCommand.execute(input);
    }
}

package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleTypeCommand;
import io.mojaloop.core.participant.contract.exception.OracleAlreadyExistsException;
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
public class ChangeOracleTypeController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeOracleTypeController.class);

    private final ChangeOracleTypeCommand changeOracleTypeCommand;

    public ChangeOracleTypeController(ChangeOracleTypeCommand changeOracleTypeCommand) {
        assert changeOracleTypeCommand != null;
        this.changeOracleTypeCommand = changeOracleTypeCommand;
    }

    @PostMapping("/oracles/change-type")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeOracleTypeCommand.Output execute(@Valid @RequestBody ChangeOracleTypeCommand.Input input)
        throws OracleAlreadyExistsException, OracleIdNotFoundException {
        return this.changeOracleTypeCommand.execute(input);
    }
}

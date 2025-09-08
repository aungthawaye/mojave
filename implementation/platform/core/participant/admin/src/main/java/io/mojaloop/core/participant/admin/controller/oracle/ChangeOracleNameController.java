package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.command.oracle.ChangeOracleNameCommand;
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
public class ChangeOracleNameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeOracleNameController.class);

    private final ChangeOracleNameCommand changeOracleNameCommand;

    public ChangeOracleNameController(ChangeOracleNameCommand changeOracleNameCommand) {
        assert changeOracleNameCommand != null;
        this.changeOracleNameCommand = changeOracleNameCommand;
    }

    @PostMapping("/oracles/change-name")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public ChangeOracleNameCommand.Output execute(@Valid @RequestBody ChangeOracleNameCommand.Input input)
        throws OracleIdNotFoundException {
        return this.changeOracleNameCommand.execute(input);
    }
}

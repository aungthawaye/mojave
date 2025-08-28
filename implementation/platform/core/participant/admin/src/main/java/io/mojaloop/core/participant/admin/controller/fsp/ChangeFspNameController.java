package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.ChangeFspNameCommand;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class ChangeFspNameController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeFspNameController.class);

    private final ChangeFspNameCommand changeFspNameCommand;

    public ChangeFspNameController(ChangeFspNameCommand changeFspNameCommand) {

        assert changeFspNameCommand != null;

        this.changeFspNameCommand = changeFspNameCommand;
    }

    @PostMapping("/fsps/change-fsp-name")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request) throws FspIdNotFoundException {

        var input = new ChangeFspNameCommand.Input(new FspId(request.fspId()), request.name());

        var output = this.changeFspNameCommand.execute(input);

        return ResponseEntity.ok(new Response());
    }

    public record Request(@NotNull Long fspId, @NotNull @NotBlank @Max(StringSizeConstraints.MAX_FSP_NAME_LENGTH) String name) { }

    public record Response() { }

}

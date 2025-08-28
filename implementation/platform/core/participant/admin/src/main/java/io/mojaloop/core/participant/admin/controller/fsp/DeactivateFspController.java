package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateFspCommand;
import io.mojaloop.core.participant.contract.exception.FspIdNotFoundException;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class DeactivateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateFspController.class);

    private final DeactivateFspCommand deactivateFspCommand;

    public DeactivateFspController(DeactivateFspCommand deactivateFspCommand) {

        assert deactivateFspCommand != null;

        this.deactivateFspCommand = deactivateFspCommand;
    }

    @PostMapping("/fsps/deactivate-fsp")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request) throws FspIdNotFoundException {

        var input = new DeactivateFspCommand.Input(new FspId(request.fspId()));

        var output = this.deactivateFspCommand.execute(input);

        return ResponseEntity.ok(new Response());
    }

    public record Request(@NotNull Long fspId) { }

    public record Response() { }

}

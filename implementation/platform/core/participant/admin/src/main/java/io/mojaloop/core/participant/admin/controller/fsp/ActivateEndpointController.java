package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.ActivateEndpointCommand;
import io.mojaloop.core.participant.contract.exception.CannotActivateEndpointException;
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
public class ActivateEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ActivateEndpointController.class);

    private final ActivateEndpointCommand activateEndpointCommand;

    public ActivateEndpointController(ActivateEndpointCommand activateEndpointCommand) {

        assert activateEndpointCommand != null;

        this.activateEndpointCommand = activateEndpointCommand;
    }

    @PostMapping("/fsps/activate-endpoint")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request)
        throws FspIdNotFoundException, CannotActivateEndpointException {

        var input = new ActivateEndpointCommand.Input(new FspId(request.fspId()), request.endpointType());

        var output = this.activateEndpointCommand.execute(input);

        return ResponseEntity.ok(new Response(output.activated()));
    }

    public record Request(@NotNull Long fspId, @NotNull EndpointType endpointType) { }

    public record Response(boolean activated) { }

}

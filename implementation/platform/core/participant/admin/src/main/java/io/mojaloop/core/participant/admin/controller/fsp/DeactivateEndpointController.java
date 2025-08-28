package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.DeactivateEndpointCommand;
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
public class DeactivateEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(DeactivateEndpointController.class);

    private final DeactivateEndpointCommand deactivateEndpointCommand;

    public DeactivateEndpointController(DeactivateEndpointCommand deactivateEndpointCommand) {

        assert deactivateEndpointCommand != null;

        this.deactivateEndpointCommand = deactivateEndpointCommand;
    }

    @PostMapping("/fsps/deactivate-endpoint")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request)
        throws FspIdNotFoundException {

        var input = new DeactivateEndpointCommand.Input(new FspId(request.fspId()), request.endpointType());

        var output = this.deactivateEndpointCommand.execute(input);

        return ResponseEntity.ok(new Response(output.deactivated()));
    }

    public record Request(@NotNull Long fspId, @NotNull EndpointType endpointType) { }

    public record Response(boolean deactivated) { }

}

package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.command.fsp.ChangeEndpointCommand;
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
public class ChangeEndpointController {

    private static final Logger LOGGER = LoggerFactory.getLogger(ChangeEndpointController.class);

    private final ChangeEndpointCommand changeEndpointCommand;

    public ChangeEndpointController(ChangeEndpointCommand changeEndpointCommand) {

        assert changeEndpointCommand != null;

        this.changeEndpointCommand = changeEndpointCommand;
    }

    @PostMapping("/fsps/change-endpoint")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request) {

        var input = new ChangeEndpointCommand.Input(new FspId(request.fspId()), request.endpointType(), request.baseUrl());

        var output = this.changeEndpointCommand.execute(input);

        return ResponseEntity.ok(new Response(output.changed()));
    }

    public record Request(@NotNull Long fspId,
                          @NotNull EndpointType endpointType,
                          @NotNull @NotBlank @Max(StringSizeConstraints.MAX_BASE_URL_LEN) String baseUrl) { }

    public record Response(boolean changed) { }

}

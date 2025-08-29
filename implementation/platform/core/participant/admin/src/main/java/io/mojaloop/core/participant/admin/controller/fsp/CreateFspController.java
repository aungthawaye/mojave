package io.mojaloop.core.participant.admin.controller.fsp;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.common.datatype.enumeration.fspiop.EndpointType;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.FspCodeAlreadyExistsException;
import io.mojaloop.fspiop.spec.core.Currency;
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
public class CreateFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFspController.class.getName());

    private final CreateFspCommand createFspCommand;

    public CreateFspController(CreateFspCommand createFspCommand) {

        assert createFspCommand != null;

        this.createFspCommand = createFspCommand;
    }

    @PostMapping("/fsps/create-fsp")
    public ResponseEntity<?> execute(@Valid @RequestBody Request request)
        throws FspCodeAlreadyExistsException, EndpointAlreadyConfiguredException, CurrencyAlreadySupportedException {

        var input = new CreateFspCommand.Input(new FspCode(request.fspCode()), request.name(), request.supportedCurrencies(),
                                               request.endpoints());

        var output = this.createFspCommand.execute(input);

        return ResponseEntity.ok(new Response(output.fspId().getId()));
    }

    public record Response(Long fspId) { }

    public record Request(@NotNull @NotBlank @Max(StringSizeConstraints.MAX_FSP_CODE_LEN) @JsonProperty(required = true) String fspCode,
                          @NotNull @NotBlank @Max(StringSizeConstraints.MAX_FSP_NAME_LENGTH) @JsonProperty(required = true) String name,
                          @JsonProperty(required = true) Currency[] supportedCurrencies,
                          @JsonProperty(required = true) CreateFspCommand.Input.Endpoint[] endpoints) {

        public record Endpoint(@JsonProperty(required = true) EndpointType type,
                               @NotNull @NotBlank @Max(StringSizeConstraints.MAX_BASE_URL_LEN)
                               @JsonProperty(required = true) String baseUrl) { }

    }

}

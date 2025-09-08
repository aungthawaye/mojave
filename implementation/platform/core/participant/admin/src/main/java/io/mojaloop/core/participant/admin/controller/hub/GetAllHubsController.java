package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetAllHubsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllHubsController.class);

    private final HubQuery hubQuery;

    public GetAllHubsController(HubQuery hubQuery) {

        assert hubQuery != null;

        this.hubQuery = hubQuery;
    }

    @GetMapping("/hubs/get-all-hubs")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<HubData> execute() {

        return this.hubQuery.getAll();
    }
}

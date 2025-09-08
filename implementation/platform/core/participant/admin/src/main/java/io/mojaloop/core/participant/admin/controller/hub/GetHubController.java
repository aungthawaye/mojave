package io.mojaloop.core.participant.admin.controller.hub;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.exception.HubIdNotFoundException;
import io.mojaloop.core.participant.contract.query.HubQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetHubController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetHubController.class.getName());

    private final HubQuery hubQuery;

    public GetHubController(HubQuery hubQuery) {

        assert hubQuery != null;

        this.hubQuery = hubQuery;
    }

    @GetMapping("/hubs/get-hub")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public HubData execute(@RequestParam Long hubId) throws HubIdNotFoundException {

        return this.hubQuery.get(new HubId(hubId));
    }
}

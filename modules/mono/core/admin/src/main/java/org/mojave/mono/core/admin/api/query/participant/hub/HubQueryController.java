package org.mojave.mono.core.admin.api.query.participant.hub;

import org.mojave.core.participant.contract.data.HubData;
import org.mojave.core.participant.contract.query.HubQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class HubQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubQueryController.class);

    private final HubQuery hubQuery;

    public HubQueryController(final HubQuery hubQuery) {

        Objects.requireNonNull(hubQuery);

        this.hubQuery = hubQuery;
    }

    @GetMapping("/participant/hubs/count")
    public ResponseEntity<Long> count() {

        return this.respond(
            LOGGER,
            "HubQuery.count",
            new HubQuery.CountInput(),
            this.hubQuery::count);
    }

    @GetMapping("/participant/hubs/get")
    public ResponseEntity<HubData> get() {

        return this.respond(
            LOGGER,
            "HubQuery.get",
            new HubQuery.GetInput(),
            this.hubQuery::get);
    }

    @GetMapping("/participant/hubs/get-all")
    public ResponseEntity<List<HubData>> getAll() {

        return this.respond(
            LOGGER,
            "HubQuery.getAll",
            new HubQuery.GetAllInput(),
            this.hubQuery::getAll);
    }

}

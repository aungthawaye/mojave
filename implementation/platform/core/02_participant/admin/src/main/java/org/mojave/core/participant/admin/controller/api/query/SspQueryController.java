package org.mojave.core.participant.admin.controller.api.query;

import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.query.SspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class SspQueryController {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        SspQueryController.class.getName());

    private final SspQuery sspQuery;

    public SspQueryController(final SspQuery sspQuery) {

        assert sspQuery != null;

        this.sspQuery = sspQuery;
    }

    @GetMapping("/ssps/get-by-ssp-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SspData get(@RequestParam final SspCode sspCode) {

        return this.sspQuery.get(sspCode);
    }

    @GetMapping("/ssps/get-by-ssp-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public SspData get(@RequestParam final SspId sspId) {

        return this.sspQuery.get(sspId);
    }

    @GetMapping("/ssps/get-all-ssps")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<SspData> getAll() {

        return this.sspQuery.getAll();
    }
}

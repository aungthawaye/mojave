package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.participant.contract.exception.fsp.FspIdNotFoundException;
import io.mojaloop.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import io.mojaloop.core.participant.contract.data.FspData;

@RestController
public class GetFspController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetFspController.class.getName());

    private final FspQuery fspQuery;

    public GetFspController(FspQuery fspQuery) {

        assert fspQuery != null;

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/get-fsp")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public FspData execute(@RequestParam Long fspId) throws FspIdNotFoundException {

        return this.fspQuery.get(new FspId(fspId));
    }

}

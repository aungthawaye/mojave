package io.mojaloop.core.participant.admin.controller.fsp;

import io.mojaloop.core.participant.contract.query.FspQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import io.mojaloop.core.participant.contract.data.FspData;

@RestController
public class GetAllFspsController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllFspsController.class);

    private final FspQuery fspQuery;

    public GetAllFspsController(FspQuery fspQuery) {

        assert fspQuery != null;

        this.fspQuery = fspQuery;
    }

    @GetMapping("/fsps/get-all-fsps")
    @ResponseStatus(HttpStatus.OK)
    public List<FspData> execute() {

        return this.fspQuery.getAll();
    }

}

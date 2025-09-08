package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class GetAllOraclesController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAllOraclesController.class);

    private final OracleQuery oracleQuery;

    public GetAllOraclesController(OracleQuery oracleQuery) {
        assert oracleQuery != null;
        this.oracleQuery = oracleQuery;
    }

    @GetMapping("/oracles/get-all-oracles")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<OracleData> execute() {
        return this.oracleQuery.getAll();
    }
}

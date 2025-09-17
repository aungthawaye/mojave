package io.mojaloop.core.participant.admin.controller.oracle;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.exception.oracle.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class GetOracleController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetOracleController.class.getName());

    private final OracleQuery oracleQuery;

    public GetOracleController(OracleQuery oracleQuery) {
        assert oracleQuery != null;
        this.oracleQuery = oracleQuery;
    }

    @GetMapping("/oracles/get-oracle")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public OracleData execute(@RequestParam Long oracleId) throws OracleIdNotFoundException {
        return this.oracleQuery.get(new OracleId(oracleId));
    }
}

package io.mojaloop.core.wallet.admin.controller.position;

import io.mojaloop.core.common.datatype.identifier.wallet.PositionId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.PositionData;
import io.mojaloop.core.wallet.contract.query.PositionQuery;
import io.mojaloop.fspiop.spec.core.Currency;
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
public class GetPositionController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetPositionController.class.getName());

    private final PositionQuery positionQuery;

    public GetPositionController(final PositionQuery positionQuery) {

        assert positionQuery != null;

        this.positionQuery = positionQuery;
    }

    @GetMapping("/positions/get-all-positions")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PositionData> allPositions() {

        return this.positionQuery.getAll();
    }

    @GetMapping("/positions/get-by-ownership")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<PositionData> byOwnership(@RequestParam(name = "currency") final Currency currency, @RequestParam(name = "owner-id") final Long ownerId) {

        return this.positionQuery.get(new WalletOwnerId(ownerId), currency);
    }

    @GetMapping("/positions/get-by-position-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PositionData byPositionId(@RequestParam(name = "position-id") final Long positionId) {

        return this.positionQuery.get(new PositionId(positionId));
    }

}

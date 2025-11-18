package io.mojaloop.core.wallet.admin.controller.wallet;

import io.mojaloop.core.common.datatype.identifier.wallet.WalletId;
import io.mojaloop.core.common.datatype.identifier.wallet.WalletOwnerId;
import io.mojaloop.core.wallet.contract.data.WalletData;
import io.mojaloop.core.wallet.contract.query.WalletQuery;
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
public class GetWalletController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetWalletController.class.getName());

    private final WalletQuery walletQuery;

    public GetWalletController(final WalletQuery walletQuery) {

        assert walletQuery != null;

        this.walletQuery = walletQuery;
    }

    @GetMapping("/wallets/get-all-wallets")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<WalletData> allWallets() {

        return this.walletQuery.getAll();
    }

    @GetMapping("/wallets/get-by-ownership")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<WalletData> byOwnership(@RequestParam(name = "currency") final Currency currency, @RequestParam(name = "owner-id") final Long ownerId) {

        return this.walletQuery.get(new WalletOwnerId(ownerId), currency);
    }

    @GetMapping("/wallets/get-by-wallet-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public WalletData byWalletId(@RequestParam(name = "wallet-id") final Long walletId) {

        return this.walletQuery.get(new WalletId(walletId));
    }

}

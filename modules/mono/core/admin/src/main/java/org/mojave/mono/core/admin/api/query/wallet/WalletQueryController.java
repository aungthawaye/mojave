package org.mojave.mono.core.admin.api.query.wallet;

import org.mojave.core.wallet.contract.data.WalletData;
import org.mojave.core.wallet.contract.query.WalletQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.identifier.wallet.WalletId;
import org.mojave.scheme.rule.identifier.wallet.WalletOwnerId;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class WalletQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(WalletQueryController.class);

    private final WalletQuery walletQuery;

    public WalletQueryController(final WalletQuery walletQuery) {

        Objects.requireNonNull(walletQuery);

        this.walletQuery = walletQuery;
    }

    @GetMapping("/wallet/get-by-id")
    public ResponseEntity<WalletData> getById(@RequestParam final WalletId walletId) {

        return this.respond(
            LOGGER,
            "WalletQuery.getById",
            new WalletQuery.GetByIdInput(walletId),
            () -> this.walletQuery.get(walletId));
    }

    @GetMapping("/wallet/get-by-owner-id-currency-tag")
    public ResponseEntity<WalletData> getByOwnerIdCurrencyTag(@RequestParam final WalletOwnerId ownerId,
                                                              @RequestParam final Currency currency,
                                                              @RequestParam final String tag) {

        return this.respond(
            LOGGER,
            "WalletQuery.getByOwnerIdCurrencyTag",
            new WalletQuery.GetByOwnerIdCurrencyTagInput(ownerId, currency, tag),
            () -> this.walletQuery.get(ownerId, currency, tag));
    }

    @GetMapping("/wallet/get-by-owner-id")
    public ResponseEntity<List<WalletData>> getByOwnerId(@RequestParam final WalletOwnerId ownerId) {

        return this.respond(
            LOGGER,
            "WalletQuery.getByOwnerId",
            new WalletQuery.GetByOwnerIdInput(ownerId),
            () -> this.walletQuery.get(ownerId));
    }

    @GetMapping("/wallet/get-by-owner-id-currency")
    public ResponseEntity<List<WalletData>> getByOwnerIdCurrency(
        @RequestParam final WalletOwnerId ownerId,
        @RequestParam final Currency currency) {

        return this.respond(
            LOGGER,
            "WalletQuery.getByOwnerIdCurrency",
            new WalletQuery.GetByOwnerIdCurrencyInput(ownerId, currency),
            () -> this.walletQuery.get(ownerId, currency));
    }

    @GetMapping("/wallet/get-all")
    public ResponseEntity<List<WalletData>> getAll() {

        return this.respond(
            LOGGER,
            "WalletQuery.getAll",
            new WalletQuery.GetAllInput(),
            this.walletQuery::getAll);
    }

}

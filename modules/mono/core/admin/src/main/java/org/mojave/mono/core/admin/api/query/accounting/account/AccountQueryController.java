package org.mojave.mono.core.admin.api.query.accounting.account;

import org.mojave.component.misc.query.PagedResult;
import org.mojave.core.accounting.contract.data.AccountData;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.mono.core.admin.api.BaseApiController;
import org.mojave.scheme.rule.identifier.accounting.AccountId;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Objects;

@RestController
public class AccountQueryController extends BaseApiController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountQueryController.class);

    private final AccountQuery accountQuery;

    public AccountQueryController(final AccountQuery accountQuery) {

        Objects.requireNonNull(accountQuery);

        this.accountQuery = accountQuery;
    }

    @PostMapping("/accounting/accounts/find")
    public ResponseEntity<PagedResult<AccountData>> find(
        @RequestBody final AccountQuery.Criteria criteria) {

        return this.respond(
            LOGGER,
            "AccountQuery.find",
            new AccountQuery.FindInput(criteria),
            () -> this.accountQuery.find(criteria));
    }

    @GetMapping("/accounting/accounts/get-by-code")
    public ResponseEntity<AccountData> getByCode(@RequestParam final AccountCode accountCode) {

        return this.respond(
            LOGGER,
            "AccountQuery.getByCode",
            new AccountQuery.GetByCodeInput(accountCode),
            () -> this.accountQuery.get(accountCode));
    }

    @GetMapping("/accounting/accounts/get-by-owner-id")
    public ResponseEntity<List<AccountData>> getByOwnerId(
        @RequestParam final AccountOwnerId ownerId) {

        return this.respond(
            LOGGER,
            "AccountQuery.getByOwnerId",
            new AccountQuery.GetByOwnerIdInput(ownerId),
            () -> this.accountQuery.get(ownerId));
    }

    @GetMapping("/accounting/accounts/get-by-id")
    public ResponseEntity<AccountData> getById(@RequestParam final AccountId accountId) {

        return this.respond(
            LOGGER,
            "AccountQuery.getById",
            new AccountQuery.GetByIdInput(accountId),
            () -> this.accountQuery.get(accountId));
    }

    @GetMapping("/accounting/accounts/get-all")
    public ResponseEntity<List<AccountData>> getAll() {

        return this.respond(
            LOGGER,
            "AccountQuery.getAll",
            new AccountQuery.GetAllInput(),
            this.accountQuery::getAll);
    }

}

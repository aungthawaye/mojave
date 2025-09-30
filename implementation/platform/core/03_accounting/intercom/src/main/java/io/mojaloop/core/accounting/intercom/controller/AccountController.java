package io.mojaloop.core.accounting.intercom.controller;

import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.query.AccountQuery;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class AccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(AccountController.class);

    private final AccountQuery accountQuery;

    public AccountController(AccountQuery accountQuery) {

        assert accountQuery != null;

        this.accountQuery = accountQuery;

    }

    @GetMapping("accounts")
    public List<AccountData> getAccounts() {

        return this.accountQuery.getAll();
    }

}

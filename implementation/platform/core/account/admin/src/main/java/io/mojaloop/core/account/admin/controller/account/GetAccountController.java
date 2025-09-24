package io.mojaloop.core.account.admin.controller.account;

import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.account.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.contract.query.AccountQuery;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
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
public class GetAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(GetAccountController.class);

    private final AccountQuery accountQuery;

    public GetAccountController(AccountQuery accountQuery) {

        assert accountQuery != null;

        this.accountQuery = accountQuery;
    }

    @GetMapping("/accounts/get-all-accounts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> allAccounts() {

        return this.accountQuery.getAll();
    }

    @GetMapping("/accounts/get-by-account-code")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData byAccountCode(@RequestParam String accountCode) throws AccountCodeNotFoundException {

        return this.accountQuery.get(new AccountCode(accountCode));
    }

    @GetMapping("/accounts/get-by-account-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public AccountData byAccountId(@RequestParam Long accountId) throws AccountIdNotFoundException {

        return this.accountQuery.get(new AccountId(accountId));
    }

    @GetMapping("/accounts/get-by-owner-id")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public List<AccountData> byOwnerId(@RequestParam Long ownerId) {

        return this.accountQuery.get(new OwnerId(ownerId));
    }

}

package io.mojaloop.core.accounting.admin.controller.account;

import io.mojaloop.component.misc.query.PagedRequest;
import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.component.misc.query.SortingMode;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.query.AccountQuery;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.OwnerId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class FindAccountController {

    private static final Logger LOGGER = LoggerFactory.getLogger(FindAccountController.class);

    private final AccountQuery accountQuery;

    public FindAccountController(AccountQuery accountQuery) {

        assert accountQuery != null;

        this.accountQuery = accountQuery;
    }

    @PostMapping("/accounts/find-accounts")
    @ResponseStatus(HttpStatus.OK)
    @ResponseBody
    public PagedResult<AccountData> findAccounts(@RequestBody Request request) {

        var criteria = request.criteria;

        return this.accountQuery.find(
            criteria.accountCode, criteria.name, criteria.ownerId, criteria.chartEntryId, criteria.currency,
            request.pagedRequest, request.sortBy.column, request.sortBy.mode);
    }

    public record Request(Criteria criteria, SortBy sortBy, PagedRequest pagedRequest) {

        public record Criteria(AccountCode accountCode,
                               String name,
                               OwnerId ownerId,
                               ChartEntryId chartEntryId,
                               Currency currency) { }

        public record SortBy(AccountQuery.Sorting.Column column, SortingMode mode) { }

    }

}

/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.accounting.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.component.misc.query.PagedRequest;
import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.component.misc.query.SortingMode;
import io.mojaloop.core.accounting.contract.data.AccountData;
import io.mojaloop.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.accounting.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.accounting.contract.query.AccountQuery;
import io.mojaloop.core.accounting.domain.model.Account;
import io.mojaloop.core.accounting.domain.repository.AccountRepository;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountId;
import io.mojaloop.core.common.datatype.identifier.accounting.AccountOwnerId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.type.accounting.AccountCode;
import io.mojaloop.fspiop.spec.core.Currency;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AccountQueryHandler implements AccountQuery {

    private final AccountRepository accountRepository;

    public AccountQueryHandler(AccountRepository accountRepository) {

        assert accountRepository != null;

        this.accountRepository = accountRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public PagedResult<AccountData> find(Criteria criteria) {

        Specification<Account> spec = Specification.unrestricted();

        var accountCode = criteria.filter().accountCode();
        if (accountCode != null) {
            spec = spec.and(AccountRepository.Filters.withCode(accountCode));
        }

        var name = criteria.filter().name();
        if (name != null && !name.isBlank()) {
            final var like = "%" + name.trim() + "%";
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), like));
        }

        var ownerId = criteria.filter().ownerId();
        if (ownerId != null) {
            spec = spec.and(AccountRepository.Filters.withOwnerId(ownerId));
        }

        var chartEntryId = criteria.filter().chartEntryId();
        if (chartEntryId != null) {
            spec = spec.and(AccountRepository.Filters.withChartEntryId(chartEntryId));
        }

        var currency = criteria.filter().currency();
        if (currency != null) {
            spec = spec.and(AccountRepository.Filters.withCurrency(currency));
        }

        final var sortProperty = switch (criteria.sortingColumn()) {
            case ACCOUNT_CODE -> "code";
            case NAME -> "name";
            case OWNER_ID -> "ownerId";
            case CHART_ENTRY_ID -> "chartEntryId";
            case CURRENCY -> "currency";
            default -> "id";
        };

        final var direction = criteria.sortingMode() == SortingMode.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;

        final var sort = Sort.by(direction, sortProperty);

        var pagedRequest = criteria.pagedRequest();
        final var page = PageRequest.of(Math.max(0, pagedRequest.pageNo() - 1), Math.max(1, pagedRequest.pageSize()), sort);

        final var resultPage = this.accountRepository.findAll(spec, page);

        final var data = resultPage.getContent().stream().map(Account::convert).toList();

        return new PagedResult<>(resultPage.getNumber() + 1, resultPage.getSize(), resultPage.getTotalPages(), (int) resultPage.getTotalElements(), data);
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public AccountData get(AccountCode accountCode) throws AccountCodeNotFoundException {

        return this.accountRepository.findOne(AccountRepository.Filters.withCode(accountCode)).orElseThrow(() -> new AccountCodeNotFoundException(accountCode)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<AccountData> get(AccountOwnerId ownerId) {

        return this.accountRepository.findAll(AccountRepository.Filters.withOwnerId(ownerId)).stream().map(Account::convert).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public AccountData get(AccountId accountId) throws AccountIdNotFoundException {

        return this.accountRepository.findById(accountId).orElseThrow(() -> new AccountIdNotFoundException(accountId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<AccountData> getAll() {

        return this.accountRepository.findAll().stream().map(Account::convert).collect(Collectors.toList());
    }

}

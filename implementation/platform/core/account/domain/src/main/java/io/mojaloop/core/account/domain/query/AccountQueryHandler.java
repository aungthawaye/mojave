package io.mojaloop.core.account.domain.query;

import io.mojaloop.component.jpa.routing.annotation.Read;
import io.mojaloop.component.misc.query.PagedRequest;
import io.mojaloop.component.misc.query.PagedResult;
import io.mojaloop.component.misc.query.SortingMode;
import io.mojaloop.core.account.contract.data.AccountData;
import io.mojaloop.core.account.contract.exception.account.AccountCodeNotFoundException;
import io.mojaloop.core.account.contract.exception.account.AccountIdNotFoundException;
import io.mojaloop.core.account.contract.query.AccountQuery;
import io.mojaloop.core.account.domain.model.Account;
import io.mojaloop.core.account.domain.repository.AccountRepository;
import io.mojaloop.core.common.datatype.identifier.account.AccountId;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.OwnerId;
import io.mojaloop.core.common.datatype.type.account.AccountCode;
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
    public PagedResult<AccountData> find(AccountCode accountCode,
                                         String name,
                                         OwnerId ownerId,
                                         ChartEntryId chartEntryId,
                                         Currency currency,
                                         PagedRequest pagedRequest,
                                         Sorting.Column sortingColumn,
                                         SortingMode sortingMode) {

        Specification<Account> spec = Specification.unrestricted();

        if (accountCode != null) {
            spec = spec.and(AccountRepository.Filters.withCode(accountCode));
        }
        if (name != null && !name.isBlank()) {
            final var like = "%" + name.trim() + "%";
            spec = spec.and((root, query, cb) -> cb.like(root.get("name"), like));
        }
        if (ownerId != null) {
            spec = spec.and(AccountRepository.Filters.withOwnerId(ownerId));
        }
        if (chartEntryId != null) {
            spec = spec.and(AccountRepository.Filters.withChartEntryId(chartEntryId));
        }
        if (currency != null) {
            spec = spec.and(AccountRepository.Filters.withCurrency(currency));
        }

        final var sortProperty = switch (sortingColumn) {
            case ACCOUNT_CODE -> "code";
            case NAME -> "name";
            case OWNER_ID -> "ownerId";
            case CHART_ENTRY_ID -> "chartEntryId";
            case CURRENCY -> "currency";
            default -> "id";
        };

        final var direction = sortingMode == SortingMode.DESC ? Sort.Direction.DESC : Sort.Direction.ASC;

        final var sort = Sort.by(direction, sortProperty);

        final var page = PageRequest.of(
            Math.max(0, pagedRequest.page() - 1), Math.max(1, pagedRequest.pageSize()), sort);

        final var resultPage = this.accountRepository.findAll(spec, page);

        final var data = resultPage.getContent().stream().map(Account::convert).toList();

        return new PagedResult<>(
            resultPage.getNumber() + 1, resultPage.getSize(), resultPage.getTotalPages(),
            (int) resultPage.getTotalElements(), data);
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public AccountData get(AccountCode accountCode) throws AccountCodeNotFoundException {

        return this.accountRepository.findOne(AccountRepository.Filters.withCode(accountCode))
                                     .orElseThrow(() -> new AccountCodeNotFoundException(accountCode))
                                     .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<AccountData> get(OwnerId ownerId) {

        return this.accountRepository.findAll(AccountRepository.Filters.withOwnerId(ownerId))
                                     .stream().map(Account::convert).collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public AccountData get(AccountId accountId) throws AccountIdNotFoundException {

        return this.accountRepository.findById(accountId)
                                     .orElseThrow(() -> new AccountIdNotFoundException(accountId))
                                     .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<AccountData> getAll() {

        return this.accountRepository.findAll().stream().map(Account::convert).collect(Collectors.toList());
    }

}

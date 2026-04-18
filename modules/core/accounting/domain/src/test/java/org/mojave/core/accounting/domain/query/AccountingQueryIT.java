package org.mojave.core.accounting.domain.query;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.mojave.core.accounting.contract.command.account.CreateAccountCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaCommand;
import org.mojave.core.accounting.contract.command.chart.CreateCoaEntryCommand;
import org.mojave.core.accounting.contract.command.definition.CreateFlowDefinitionCommand;
import org.mojave.core.accounting.contract.data.CoaData;
import org.mojave.core.accounting.contract.data.FlowDefinitionData;
import org.mojave.core.accounting.contract.exception.account.AccountCodeNotFoundException;
import org.mojave.core.accounting.contract.exception.account.AccountIdNotFoundException;
import org.mojave.core.accounting.contract.exception.chart.CoaEntryIdNotFoundException;
import org.mojave.core.accounting.contract.exception.chart.CoaIdNotFoundException;
import org.mojave.core.accounting.contract.exception.definition.FlowDefinitionNotFoundException;
import org.mojave.core.accounting.contract.query.AccountQuery;
import org.mojave.core.accounting.contract.query.CoaEntryQuery;
import org.mojave.core.accounting.contract.query.CoaQuery;
import org.mojave.core.accounting.contract.query.FlowDefinitionQuery;
import org.mojave.core.accounting.domain.AccountingDomainTestConfiguration;
import org.mojave.core.accounting.domain.BaseIT;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.accounting.AccountType;
import org.mojave.scheme.rule.enums.accounting.Side;
import org.mojave.scheme.rule.identifier.accounting.AccountId;
import org.mojave.scheme.rule.identifier.accounting.AccountOwnerId;
import org.mojave.scheme.rule.identifier.accounting.CoaEntryId;
import org.mojave.scheme.rule.identifier.accounting.CoaId;
import org.mojave.scheme.rule.identifier.accounting.FlowDefinitionId;
import org.mojave.scheme.rule.type.accounting.AccountCode;
import org.mojave.component.misc.ddd.EntityId;
import org.mojave.component.misc.query.PagedRequest;
import org.mojave.component.misc.query.SortingMode;
import org.mojave.scheme.rule.scenario.ScenarioType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.Comparator;
import java.util.List;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        AccountingDomainTestConfiguration.class})
@DisplayName("Accounting Queries Integration Test")
public class AccountingQueryIT extends BaseIT {

    @Autowired
    private AccountQuery accountQuery;

    @Autowired
    private CoaEntryQuery coaEntryQuery;

    @Autowired
    private CoaQuery coaQuery;

    @Autowired
    private CreateAccountCommand createAccountCommand;

    @Autowired
    private CreateCoaCommand createCoaCommand;

    @Autowired
    private CreateCoaEntryCommand createCoaEntryCommand;

    @Autowired
    private CreateFlowDefinitionCommand createFlowDefinitionCommand;

    @Autowired
    private FlowDefinitionQuery flowDefinitionQuery;

    private static Stream<Arguments> accountSortingArguments() {

        return Stream.of(
            Arguments.of(AccountQuery.Sorting.Column.ID, SortingMode.DESC, "ACCOUNT_QUERY_B"),
            Arguments.of(
                AccountQuery.Sorting.Column.ACCOUNT_CODE, SortingMode.ASC,
                "ACCOUNT_QUERY_A"),
            Arguments.of(AccountQuery.Sorting.Column.NAME, SortingMode.ASC, "ACCOUNT_QUERY_A"),
            Arguments.of(AccountQuery.Sorting.Column.OWNER_ID, SortingMode.ASC, "ACCOUNT_QUERY_A"),
            Arguments.of(
                AccountQuery.Sorting.Column.COA_ENTRY_ID, SortingMode.DESC,
                "ACCOUNT_QUERY_B"),
            Arguments.of(
                AccountQuery.Sorting.Column.CURRENCY, SortingMode.DESC, "ACCOUNT_QUERY_B"));
    }

    @ParameterizedTest(name = "Sort accounts by {0} {1}")
    @MethodSource("accountSortingArguments")
    public void accountFindSortsByColumn(final AccountQuery.Sorting.Column column,
                                         final SortingMode sortingMode,
                                         final String expectedCode) {

        this.createAccountQueryFixture();

        final var result = this.accountQuery.find(new AccountQuery.Criteria(
            new AccountQuery.Criteria.Filter(null, null, null, null, null), new PagedRequest(1, 10),
            column, sortingMode));

        assertEquals(expectedCode, result.data().getFirst().code().value());
    }

    @Test
    @DisplayName("Find accounts with filters")
    public void accountFindWithFilters() {

        final var fixture = this.createAccountQueryFixture();

        final var result = this.accountQuery.find(new AccountQuery.Criteria(
            new AccountQuery.Criteria.Filter(
                new AccountCode("ACCOUNT_QUERY_B"), " Second Account ", new AccountOwnerId(1202L),
                fixture.secondCoaEntryId(), Currency.USD), new PagedRequest(1, 10),
            AccountQuery.Sorting.Column.NAME, SortingMode.ASC));

        assertEquals(1, result.totalRecords());
        assertEquals(fixture.secondAccountId(), result.data().getFirst().accountId());
    }

    @Test
    @DisplayName("Find accounts without filters and use paging defaults")
    public void accountFindWithoutFilters() {

        this.createAccountQueryFixture();

        final var result = this.accountQuery.find(new AccountQuery.Criteria(
            new AccountQuery.Criteria.Filter(null, "   ", null, null, null), new PagedRequest(1, 1),
            AccountQuery.Sorting.Column.ID, SortingMode.ASC));

        assertEquals(1, result.pageNo());
        assertEquals(1, result.pageSize());
        assertEquals(2, result.totalRecords());
        assertEquals(2, result.totalPages());
        assertEquals(1, result.data().size());
    }

    @Test
    @DisplayName("Throw when getting account by missing code")
    public void accountGetByCodeNotFound() {

        assertThrows(
            AccountCodeNotFoundException.class,
            () -> this.accountQuery.get(new AccountCode("MISSING_ACCOUNT_CODE")));
    }

    @Test
    @DisplayName("Get account by code successfully")
    public void accountGetByCodeSuccessful() {

        final var fixture = this.createAccountQueryFixture();

        final var account = this.accountQuery.get(new AccountCode("ACCOUNT_QUERY_B"));

        assertEquals(fixture.secondAccountId(), account.accountId());
    }

    @Test
    @DisplayName("Throw when getting account by missing id")
    public void accountGetByIdNotFound() {

        assertThrows(
            AccountIdNotFoundException.class,
            () -> this.accountQuery.get(new AccountId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Get account by owner, id and all accounts")
    public void accountGetByOwnerIdAndAll() {

        final var fixture = this.createAccountQueryFixture();

        final var byOwner = this.accountQuery.get(new AccountOwnerId(1202L));
        final var byId = this.accountQuery.get(fixture.secondAccountId());
        final var all = this.accountQuery.getAll();

        assertEquals(1, byOwner.size());
        assertEquals(fixture.secondAccountId(), byOwner.getFirst().accountId());
        assertEquals(fixture.secondAccountId(), byId.accountId());
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Throw when querying missing CoA entry")
    public void coaEntryQueryNotFound() {

        assertThrows(
            CoaEntryIdNotFoundException.class,
            () -> this.coaEntryQuery.get(new CoaEntryId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Query CoA entries successfully")
    public void coaEntryQuerySuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Entry Query CoA");

        final var firstCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            "ENTRY_QUERY_01", "Entry Query One", AccountType.ASSET);

        final var secondCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "HUB",
            "ENTRY_QUERY_02", "Entry Query Two", AccountType.REVENUE);

        final var byId = this.coaEntryQuery.get(firstCoaEntryId);
        final var byCoa = this.coaEntryQuery.get(coaId);
        final var byCategory = this.coaEntryQuery.get("HUB");
        final var all = this.coaEntryQuery.getAll();

        assertEquals(firstCoaEntryId, byId.coaEntryId());
        assertEquals(2, byCoa.size());
        assertEquals(secondCoaEntryId, byCategory.getFirst().coaEntryId());
        assertEquals(2, all.size());
    }

    @Test
    @DisplayName("Throw when querying missing CoA")
    public void coaQueryNotFound() {

        assertThrows(
            CoaIdNotFoundException.class,
            () -> this.coaQuery.get(new CoaId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Query CoA successfully")
    public void coaQuerySuccessful() {

        final var firstCoaId = this.createCoa(this.createCoaCommand, "Treasury Query CoA");
        final var secondCoaId = this.createCoa(this.createCoaCommand, "Operations Query CoA");

        final var byId = this.coaQuery.get(firstCoaId);
        final var all = this.coaQuery.getAll();
        final var byName = this.coaQuery.getByNameContains("Query");

        assertEquals(firstCoaId, byId.coaId());
        assertEquals(2, all.size());
        assertEquals(
            List.of(firstCoaId, secondCoaId), byName
                                                  .stream()
                                                  .map(CoaData::coaId)
                                                  .sorted(
                                                      (left, right) -> Long.compare(
                                                          left.getId(), right.getId()))
                                                  .toList());
    }

    @Test
    @DisplayName("Throw when querying missing flow definition")
    public void flowDefinitionQueryNotFound() {

        assertThrows(
            FlowDefinitionNotFoundException.class,
            () -> this.flowDefinitionQuery.get(new FlowDefinitionId(Long.MAX_VALUE)));
    }

    @Test
    @DisplayName("Query flow definitions successfully")
    public void flowDefinitionQuerySuccessful() {

        final var coaId = this.createCoa(this.createCoaCommand, "Flow Query CoA");

        final var usdCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            "FLOW_QUERY_01", "Flow Query One", AccountType.ASSET);

        final var eurCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP",
            "FLOW_QUERY_02", "Flow Query Two", AccountType.REVENUE);

        this.createAccount(
            this.createAccountCommand, usdCoaEntryId, 1301L, Currency.USD, "FLOW_QUERY_ACC_01",
            "Flow Query Account 01");

        this.createAccount(
            this.createAccountCommand, eurCoaEntryId, 1302L, Currency.EUR, "FLOW_QUERY_ACC_02",
            "Flow Query Account 02");

        final var firstOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                ScenarioType.P2P_TRANSFER, Currency.USD, "flow-query-usd",
                "flow-query-usd description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYER_FSP", usdCoaEntryId,
                    "TRANSFER_AMOUNT", Side.DEBIT, "usd line"))));

        final var secondOutput = this.createFlowDefinitionCommand.execute(
            new CreateFlowDefinitionCommand.Input(
                ScenarioType.P2P_TRANSFER, Currency.EUR, "flow-query-eur",
                "flow-query-eur description", List.of(
                new CreateFlowDefinitionCommand.Input.FlowDefinitionLine(
                    1, "PAYER_FSP", eurCoaEntryId,
                    "TRANSFER_AMOUNT", Side.DEBIT, "eur line"))));

        final var byId = this.flowDefinitionQuery.get(firstOutput.flowDefinitionId());
        final var all = this.flowDefinitionQuery.getAll();
        final var byName = this.flowDefinitionQuery.getByNameContains("flow-query");

        assertEquals(firstOutput.flowDefinitionId(), byId.flowDefinitionId());
        assertEquals(2, all.size());
        assertEquals(2, byName.size());
        assertEquals(
            List.of(firstOutput.flowDefinitionId(), secondOutput.flowDefinitionId()), byName
                                                                                          .stream()
                                                                                          .map(
                                                                                              FlowDefinitionData::flowDefinitionId)
                                                                                          .sorted(
                                                                                              Comparator.comparingLong(
                                                                                                  EntityId::getId))
                                                                                          .toList());
    }

    private AccountQueryFixture createAccountQueryFixture() {

        final var coaId = this.createCoa(this.createCoaCommand, "Account Query CoA");

        final var firstCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "FSP", "ACCOUNT_QUERY_ENTRY_01",
            "Account Query Entry One", AccountType.ASSET);

        final var secondCoaEntryId = this.createCoaEntry(
            this.createCoaEntryCommand, coaId, "HUB", "ACCOUNT_QUERY_ENTRY_02",
            "Account Query Entry Two", AccountType.REVENUE);

        final var firstAccountId = this.createAccount(
            this.createAccountCommand, firstCoaEntryId, 1201L, Currency.EUR, "ACCOUNT_QUERY_A",
            "First Account");

        final var secondAccountId = this.createAccount(
            this.createAccountCommand, secondCoaEntryId, 1202L, Currency.USD, "ACCOUNT_QUERY_B",
            "Second Account");

        return new AccountQueryFixture(
            firstAccountId, secondAccountId, firstCoaEntryId,
            secondCoaEntryId);
    }

    private record AccountQueryFixture(AccountId firstAccountId,
                                       AccountId secondAccountId,
                                       CoaEntryId firstCoaEntryId,
                                       CoaEntryId secondCoaEntryId) { }

}

package org.mojave.accounting.domain;

import org.mojave.accounting.contract.data.AccountData;
import org.mojave.accounting.contract.data.CoaEntryData;
import org.mojave.accounting.contract.data.FlowDefinitionData;
import org.mojave.accounting.domain.cache.AccountCache;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.accounting.domain.model.Account;
import org.mojave.accounting.domain.model.CoaEntry;
import org.mojave.accounting.domain.model.FlowDefinition;
import org.mojave.accounting.domain.repository.AccountRepository;
import org.mojave.accounting.domain.repository.CoaEntryRepository;
import org.mojave.accounting.domain.repository.FlowDefinitionRepository;
import org.mojave.common.datatype.enums.Currency;
import org.mojave.common.datatype.identifier.accounting.AccountId;
import org.mojave.common.datatype.identifier.accounting.AccountOwnerId;
import org.mojave.common.datatype.identifier.accounting.CoaEntryId;
import org.mojave.common.datatype.identifier.accounting.CoaId;
import org.mojave.common.datatype.identifier.accounting.FlowDefinitionId;
import org.mojave.common.datatype.type.accounting.AccountCode;
import org.mojave.common.datatype.type.accounting.CoaEntryCode;
import org.mojave.scheme.rule.accounting.scenario.AccountingScenario;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;

import java.util.Set;
import java.util.stream.Collectors;

public class AccountingDomainDependencies
    implements AccountingDomainConfiguration.RequiredDependencies {

    @Autowired
    private AccountRepository accountRepository;

    @Autowired
    private CoaEntryRepository coaEntryRepository;

    @Autowired
    private FlowDefinitionRepository flowDefinitionRepository;

    @Bean
    @Override
    public AccountCache accountCache() {

        return new RepositoryBackedAccountCache(this.accountRepository);
    }

    @Bean
    @Override
    public CoaEntryCache coaEntryCache() {

        return new RepositoryBackedCoaEntryCache(this.coaEntryRepository);
    }

    @Bean
    @Override
    public FlowDefinitionCache flowDefinitionCache() {

        return new RepositoryBackedFlowDefinitionCache(this.flowDefinitionRepository);
    }

    @Bean
    @Override
    public AccountingDomainSettings.TestLedgerEngine ledger() {

        return new AccountingDomainSettings.TestLedgerEngine();
    }

    private record RepositoryBackedAccountCache(AccountRepository accountRepository)
        implements AccountCache {

        @Override
        public void clear() {

        }

        @Override
        public void delete(final AccountId accountId) {

        }

        @Override
        public AccountData get(final AccountCode accountCode) {

            return this.accountRepository
                       .findOne(AccountRepository.Filters.withCode(accountCode))
                       .map(Account::convert)
                       .orElse(null);
        }

        @Override
        public Set<AccountData> get(final AccountOwnerId ownerId) {

            return this.accountRepository
                       .findAll(AccountRepository.Filters.withOwnerId(ownerId))
                       .stream()
                       .map(Account::convert)
                       .collect(Collectors.toSet());
        }

        @Override
        public AccountData get(final AccountId accountId) {

            return this.accountRepository.findById(accountId).map(Account::convert).orElse(null);
        }

        @Override
        public AccountData get(final CoaEntryId coaEntryId,
                               final AccountOwnerId ownerId,
                               final Currency currency) {

            return this.accountRepository
                       .findOne(AccountRepository.Filters
                                    .withCoaEntryId(coaEntryId)
                                    .and(AccountRepository.Filters.withOwnerId(ownerId))
                                    .and(AccountRepository.Filters.withCurrency(currency)))
                       .map(Account::convert)
                       .orElse(null);
        }

        @Override
        public Set<AccountData> get(final CoaEntryId coaEntryId) {

            return this.accountRepository
                       .findAll(AccountRepository.Filters.withCoaEntryId(coaEntryId))
                       .stream()
                       .map(Account::convert)
                       .collect(Collectors.toSet());
        }

        @Override
        public void save(final AccountData account) {

        }

    }

    private record RepositoryBackedCoaEntryCache(CoaEntryRepository coaEntryRepository)
        implements CoaEntryCache {

        @Override
        public void clear() {

        }

        @Override
        public void delete(final CoaEntryId coaEntryId) {

        }

        @Override
        public CoaEntryData get(final CoaEntryId coaEntryId) {

            return this.coaEntryRepository.findById(coaEntryId).map(CoaEntry::convert).orElse(null);
        }

        @Override
        public CoaEntryData get(final CoaEntryCode code) {

            return this.coaEntryRepository
                       .findOne(CoaEntryRepository.Filters.withCode(code))
                       .map(CoaEntry::convert)
                       .orElse(null);
        }

        @Override
        public Set<CoaEntryData> get(final CoaId coaId) {

            return this.coaEntryRepository
                       .findAll(CoaEntryRepository.Filters.withCoaId(coaId))
                       .stream()
                       .map(CoaEntry::convert)
                       .collect(Collectors.toSet());
        }

        @Override
        public void save(final CoaEntryData coaEntry) {

        }

    }

    private record RepositoryBackedFlowDefinitionCache(FlowDefinitionRepository flowDefinitionRepository)
        implements FlowDefinitionCache {

        @Override
        public void clear() {

        }

        @Override
        public void delete(final FlowDefinitionId flowDefinitionId) {

        }

        @Override
        public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

            return this.flowDefinitionRepository
                       .findById(flowDefinitionId)
                       .map(FlowDefinition::convert)
                       .orElse(null);
        }

        @Override
        public FlowDefinitionData get(final AccountingScenario transactionType,
                                      final Currency currency) {

            return this.flowDefinitionRepository
                       .findOne(FlowDefinitionRepository.Filters
                                    .withScenario(transactionType)
                                    .and(FlowDefinitionRepository.Filters.withCurrency(currency)))
                       .map(FlowDefinition::convert)
                       .orElse(null);
        }

        @Override
        public void save(final FlowDefinitionData flowDefinition) {

        }

    }

}

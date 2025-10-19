package io.mojaloop.core.accounting.domain.cache.local;

import io.mojaloop.core.accounting.contract.data.FlowDefinitionData;
import io.mojaloop.core.accounting.domain.cache.FlowDefinitionCache;
import io.mojaloop.core.accounting.domain.repository.FlowDefinitionRepository;
import io.mojaloop.core.common.datatype.enums.trasaction.TransactionType;
import io.mojaloop.core.common.datatype.identifier.accounting.FlowDefinitionId;
import io.mojaloop.fspiop.spec.core.Currency;
import jakarta.annotation.PostConstruct;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class FlowDefinitionLocalCache implements FlowDefinitionCache {

    private final FlowDefinitionRepository flowDefinitionRepository;

    private final Map<Long, FlowDefinitionData> withId;

    private final Map<String, FlowDefinitionData> withTxnTypeCurrency;

    public FlowDefinitionLocalCache(final FlowDefinitionRepository flowDefinitionRepository) {

        assert flowDefinitionRepository != null;

        this.flowDefinitionRepository = flowDefinitionRepository;

        this.withId = new ConcurrentHashMap<>();
        this.withTxnTypeCurrency = new ConcurrentHashMap<>();
    }

    @Override
    public void clear() {

        this.withId.clear();
        this.withTxnTypeCurrency.clear();
    }

    @Override
    public void delete(final FlowDefinitionId flowDefinitionId) {

        final var removed = this.withId.remove(flowDefinitionId.getId());

        if (removed != null) {
            final var key = FlowDefinitionCache.Keys.forTransaction(removed.transactionType(), removed.currency());
            this.withTxnTypeCurrency.remove(key);
        }
    }

    @Override
    public FlowDefinitionData get(final FlowDefinitionId flowDefinitionId) {

        return this.withId.get(flowDefinitionId.getId());
    }

    @Override
    public FlowDefinitionData get(final TransactionType transactionType, final Currency currency) {

        final var key = FlowDefinitionCache.Keys.forTransaction(transactionType, currency);
        return this.withTxnTypeCurrency.get(key);
    }

    @PostConstruct
    public void postConstruct() {

        this.clear();

        final var defs = this.flowDefinitionRepository.findAll();

        for (final var def : defs) {
            this.save(def.convert());
        }
    }

    @Override
    public void save(final FlowDefinitionData flowDefinition) {

        this.withId.put(flowDefinition.flowDefinitionId().getId(), flowDefinition);
        final var key = FlowDefinitionCache.Keys.forTransaction(flowDefinition.transactionType(), flowDefinition.currency());
        this.withTxnTypeCurrency.put(key, flowDefinition);
    }

}

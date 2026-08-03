package org.mojave.core.settlement.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementModelData;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementModelQuery;
import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementModelQueryHandler implements SettlementModelQuery {

    private final SettlementModelRepository settlementModelRepository;

    public SettlementModelQueryHandler(final SettlementModelRepository settlementModelRepository) {

        Objects.requireNonNull(settlementModelRepository);

        this.settlementModelRepository = settlementModelRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementModelData get(final SettlementModelId settlementModelId)
        throws SettlementModelNotFoundException {

        return this.settlementModelRepository
                   .findById(settlementModelId)
                   .orElseThrow(() -> new SettlementModelNotFoundException(settlementModelId))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementModelData> getAll() {

        return this.settlementModelRepository
                   .findAll()
                   .stream()
                   .map(SettlementModel::convert)
                   .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementModelData> getByNameContains(final String name) {

        return this.settlementModelRepository
                   .findAll(SettlementModelRepository.Filters.withNameContains(name))
                   .stream()
                   .map(SettlementModel::convert)
                   .toList();
    }

}

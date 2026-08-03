package org.mojave.core.settlement.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.settlement.contract.data.SettlementWindowData;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.contract.exception.window.SettlementWindowNotFoundException;
import org.mojave.core.settlement.contract.query.SettlementWindowQuery;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.mojave.core.settlement.domain.repository.SettlementWindowRepository;
import org.mojave.scheme.rule.enums.Currency;
import org.mojave.scheme.rule.enums.settlement.SettlementWindowStatus;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.scheme.rule.identifier.settlement.SettlementModelId;
import org.mojave.scheme.rule.identifier.settlement.SettlementWindowId;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

@Service
public class SettlementWindowQueryHandler implements SettlementWindowQuery {

    private final SettlementModelRepository settlementModelRepository;

    private final SettlementWindowRepository settlementWindowRepository;

    public SettlementWindowQueryHandler(final SettlementModelRepository settlementModelRepository,
                                        final SettlementWindowRepository settlementWindowRepository) {

        Objects.requireNonNull(settlementModelRepository);
        Objects.requireNonNull(settlementWindowRepository);

        this.settlementModelRepository = settlementModelRepository;
        this.settlementWindowRepository = settlementWindowRepository;
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementWindowData get(final SettlementWindowId settlementWindowId)
        throws SettlementWindowNotFoundException {

        return this.settlementWindowRepository
                   .findById(settlementWindowId)
                   .orElseThrow(() -> new SettlementWindowNotFoundException(settlementWindowId))
                   .convert();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public List<SettlementWindowData> getAll() {

        return this.settlementWindowRepository
                   .findAll()
                   .stream()
                   .map(SettlementWindow::convert)
                   .toList();
    }

    @Override
    @Transactional(readOnly = true)
    @Read
    public SettlementWindowData getOpen(final SettlementModelId settlementModelId,
                                        final Currency currency, final FspGroupId fspGroupId)
        throws SettlementWindowNotFoundException {

        final var settlementModel = this.settlementModelRepository
                                        .findById(settlementModelId)
                                        .orElseThrow(() -> new SettlementModelNotFoundException(
                                            settlementModelId));

        final var filter = SettlementWindowRepository.Filters
                               .withSettlementModel(settlementModel)
                               .and(SettlementWindowRepository.Filters.withCurrency(currency))
                               .and(SettlementWindowRepository.Filters.withFspGroupId(fspGroupId))
                               .and(SettlementWindowRepository.Filters.withStatus(
                                   SettlementWindowStatus.OPEN));

        return this.settlementWindowRepository
                   .findOne(filter)
                   .orElseThrow(
                       () -> new SettlementWindowNotFoundException(new SettlementWindowId(0L)))
                   .convert();
    }

}

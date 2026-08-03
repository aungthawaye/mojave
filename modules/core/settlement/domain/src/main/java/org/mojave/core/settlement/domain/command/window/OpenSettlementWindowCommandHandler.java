package org.mojave.core.settlement.domain.command.window;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.window.OpenSettlementWindowCommand;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.mojave.core.settlement.domain.repository.SettlementWindowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class OpenSettlementWindowCommandHandler implements OpenSettlementWindowCommand {

    private final SettlementModelRepository settlementModelRepository;

    private final SettlementWindowRepository settlementWindowRepository;

    public OpenSettlementWindowCommandHandler(
        final SettlementModelRepository settlementModelRepository,
        final SettlementWindowRepository settlementWindowRepository) {

        Objects.requireNonNull(settlementModelRepository);
        Objects.requireNonNull(settlementWindowRepository);

        this.settlementModelRepository = settlementModelRepository;
        this.settlementWindowRepository = settlementWindowRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        final var settlementModel = this.settlementModelRepository
                                        .findById(input.settlementModelId())
                                        .orElseThrow(() -> new SettlementModelNotFoundException(
                                            input.settlementModelId()));

        final var settlementWindow = this.settlementWindowRepository.save(
            new SettlementWindow(
                settlementModel, input.currency(), input.fspGroupId(),
                input.periodStartAt()));

        return new Output(settlementWindow.getId());
    }

}

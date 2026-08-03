package org.mojave.core.settlement.domain.command.window;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.window.CloseSettlementWindowCommand;
import org.mojave.core.settlement.contract.exception.window.SettlementWindowNotFoundException;
import org.mojave.core.settlement.domain.model.SettlementWindow;
import org.mojave.core.settlement.domain.repository.SettlementWindowRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CloseSettlementWindowCommandHandler implements CloseSettlementWindowCommand {

    private final SettlementWindowRepository settlementWindowRepository;

    public CloseSettlementWindowCommandHandler(
        final SettlementWindowRepository settlementWindowRepository) {

        Objects.requireNonNull(settlementWindowRepository);

        this.settlementWindowRepository = settlementWindowRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        final var settlementWindow = this.settlementWindowRepository
                                         .findById(input.settlementWindowId())
                                         .orElseThrow(() -> new SettlementWindowNotFoundException(
                                             input.settlementWindowId()));

        settlementWindow.close(input.periodEndAt());

        final var newSettlementWindow = this.settlementWindowRepository.save(
            new SettlementWindow(
                settlementWindow.getSettlementModel(),
                settlementWindow.getCurrency(),
                settlementWindow.getFspGroupId(),
                input.periodEndAt()));

        return new Output(settlementWindow.getId(), newSettlementWindow.getId());
    }

}

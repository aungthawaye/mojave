package org.mojave.core.settlement.domain.command.model;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.model.TerminateSettlementModelCommand;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class TerminateSettlementModelCommandHandler implements TerminateSettlementModelCommand {

    private final SettlementModelRepository settlementModelRepository;

    public TerminateSettlementModelCommandHandler(
        final SettlementModelRepository settlementModelRepository) {

        Objects.requireNonNull(settlementModelRepository);

        this.settlementModelRepository = settlementModelRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        final var settlementModel = this.settlementModelRepository
                                        .findById(input.settlementModelId())
                                        .orElseThrow(() -> new SettlementModelNotFoundException(
                                            input.settlementModelId()));

        settlementModel.terminate();

        return new Output(settlementModel.getId());
    }

}

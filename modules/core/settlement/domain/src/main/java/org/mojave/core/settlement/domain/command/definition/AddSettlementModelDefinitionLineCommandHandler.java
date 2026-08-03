package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.definition.AddSettlementModelDefinitionLineCommand;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementModelDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AddSettlementModelDefinitionLineCommandHandler
    implements AddSettlementModelDefinitionLineCommand {

    private final SettlementModelDefinitionRepository settlementModelDefinitionRepository;

    public AddSettlementModelDefinitionLineCommandHandler(
        final SettlementModelDefinitionRepository settlementModelDefinitionRepository) {

        Objects.requireNonNull(settlementModelDefinitionRepository);

        this.settlementModelDefinitionRepository = settlementModelDefinitionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        final var definition = this.settlementModelDefinitionRepository
                                   .findById(input.settlementModelDefinitionId())
                                   .orElseThrow(
                                       () -> new SettlementModelDefinitionNotFoundException(
                                           input.settlementModelDefinitionId()));

        final var line = definition.addSettlementModelDefinitionLine(
            input.step(), input.participant(), input.amountName(), input.currency(),
            input.direction(), input.description());

        return new Output(definition.getId(), line.getId());
    }

}

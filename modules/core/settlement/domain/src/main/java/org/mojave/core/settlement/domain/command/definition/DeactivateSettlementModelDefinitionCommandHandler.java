package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.core.settlement.contract.command.definition.DeactivateSettlementModelDefinitionCommand;
import org.mojave.core.settlement.contract.exception.definition.SettlementModelDefinitionNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementModelDefinitionRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class DeactivateSettlementModelDefinitionCommandHandler
    implements DeactivateSettlementModelDefinitionCommand {

    private final SettlementModelDefinitionRepository settlementModelDefinitionRepository;

    public DeactivateSettlementModelDefinitionCommandHandler(
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

        definition.deactivate();

        return new Output(definition.getId());
    }

}

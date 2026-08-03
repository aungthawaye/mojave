package org.mojave.core.settlement.domain.command.model;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.model.ChangeSettlementModelPropertiesCommand;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNameTakenException;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNotFoundException;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class ChangeSettlementModelPropertiesCommandHandler
    implements ChangeSettlementModelPropertiesCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        ChangeSettlementModelPropertiesCommandHandler.class);

    private final SettlementModelRepository settlementModelRepository;

    public ChangeSettlementModelPropertiesCommandHandler(
        final SettlementModelRepository settlementModelRepository) {

        Objects.requireNonNull(settlementModelRepository);

        this.settlementModelRepository = settlementModelRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info(
            "ChangeSettlementModelPropertiesCommand : input: ({})", ObjectLogger.log(input));

        final var settlementModel = this.settlementModelRepository
                                        .findById(input.settlementModelId())
                                        .orElseThrow(() -> new SettlementModelNotFoundException(
                                            input.settlementModelId()));

        this.settlementModelRepository
            .findOne(SettlementModelRepository.Filters.withNameEquals(input.name()))
            .filter(existing -> !existing.getId().equals(input.settlementModelId()))
            .ifPresent(existing -> {
                throw new SettlementModelNameTakenException(input.name());
            });

        settlementModel
            .name(input.name())
            .settlementMethod(input.settlementMethod())
            .sspId(input.sspId());

        final var output = new Output(settlementModel.getId());

        LOGGER.info(
            "ChangeSettlementModelPropertiesCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

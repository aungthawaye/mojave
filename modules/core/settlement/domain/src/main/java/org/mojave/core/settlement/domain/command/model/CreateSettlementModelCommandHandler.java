package org.mojave.core.settlement.domain.command.model;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.model.CreateSettlementModelCommand;
import org.mojave.core.settlement.contract.exception.model.SettlementModelNameTakenException;
import org.mojave.core.settlement.domain.model.SettlementModel;
import org.mojave.core.settlement.domain.repository.SettlementModelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateSettlementModelCommandHandler implements CreateSettlementModelCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateSettlementModelCommandHandler.class);

    private final SettlementModelRepository settlementModelRepository;

    public CreateSettlementModelCommandHandler(
        final SettlementModelRepository settlementModelRepository) {

        Objects.requireNonNull(settlementModelRepository);

        this.settlementModelRepository = settlementModelRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateSettlementModelCommand : input: ({})", ObjectLogger.log(input));

        if (this.settlementModelRepository
                .findOne(SettlementModelRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new SettlementModelNameTakenException(input.name());
        }

        final var settlementModel = this.settlementModelRepository.save(
            new SettlementModel(input.name(), input.settlementMethod(), input.sspId()));
        final var output = new Output(settlementModel.getId());

        LOGGER.info("CreateSettlementModelCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

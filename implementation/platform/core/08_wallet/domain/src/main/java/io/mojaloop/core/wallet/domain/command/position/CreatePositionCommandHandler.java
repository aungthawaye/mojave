package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.core.wallet.contract.command.position.CreatePositionCommand;
import io.mojaloop.core.wallet.contract.exception.position.PositionAlreadyExistsException;
import io.mojaloop.core.wallet.domain.model.Position;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreatePositionCommandHandler implements CreatePositionCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreatePositionCommandHandler.class);

    private final PositionRepository positionRepository;

    public CreatePositionCommandHandler(final PositionRepository positionRepository) {

        assert positionRepository != null;
        this.positionRepository = positionRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("Executing CreatePositionCommand with input: {}", input);

        // Build uniqueness spec: walletOwnerId + currency
        final var spec = PositionRepository.Filters.withOwnerId(input.walletOwnerId())
                                                   .and(PositionRepository.Filters.withCurrency(input.currency()));

        if (this.positionRepository.findOne(spec).isPresent()) {
            LOGGER.info(
                "Position already exists for ownerId: {} and currency: {}", input.walletOwnerId(),
                input.currency());
            throw new PositionAlreadyExistsException(input.walletOwnerId(), input.currency());
        }

        final var position = new Position(input.walletOwnerId(), input.currency(), input.name(), input.netDebitCap());
        LOGGER.info("Created Position: {}", position);

        final var saved = this.positionRepository.save(position);
        LOGGER.info("Saved Position with id: {}", saved.getId());

        LOGGER.info("Completed CreatePositionCommand with input: {}", input);

        return new Output(saved.getId());
    }

}

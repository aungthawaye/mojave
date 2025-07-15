package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.common.component.persistence.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.domain.model.Fsp;
import io.mojaloop.core.participant.domain.model.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class CreateFspCommandHandler implements CreateFspCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(CreateFspCommandHandler.class);

    private final FspRepository fspRepository;

    public CreateFspCommandHandler(FspRepository fspRepository) {

        assert fspRepository != null;

        this.fspRepository = fspRepository;
    }

    @Override
    @Transactional
    @Write
    public CreateFspCommand.Output execute(CreateFspCommand.Input input) throws CurrencyAlreadySupportedException {

        LOGGER.info("Executing CreateFspCommand with input: {}", input);

        var fsp = new Fsp(input.fspCode(), input.name());

        for (var currency : input.supportedCurrencies()) {

            fsp.addSupportedCurrency(currency);
        }

        this.fspRepository.save(fsp);
        LOGGER.info("Completed CreateFspCommand with input: {}", input);

        return new Output(fsp.getId());
    }

}

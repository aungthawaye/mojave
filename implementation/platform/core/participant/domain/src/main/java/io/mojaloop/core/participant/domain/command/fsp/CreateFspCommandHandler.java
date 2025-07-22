package io.mojaloop.core.participant.domain.command.fsp;

import io.mojaloop.common.component.persistence.routing.annotation.Write;
import io.mojaloop.core.participant.contract.command.fsp.CreateFspCommand;
import io.mojaloop.core.participant.contract.exception.CurrencyAlreadySupportedException;
import io.mojaloop.core.participant.contract.exception.EndpointAlreadyConfiguredException;
import io.mojaloop.core.participant.contract.exception.FspAlreadyExistsException;
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
    public CreateFspCommand.Output execute(CreateFspCommand.Input input)
        throws CurrencyAlreadySupportedException, EndpointAlreadyConfiguredException, FspAlreadyExistsException {

        LOGGER.info("Executing CreateFspCommand with input: {}", input);

        if (this.fspRepository.findOne(FspRepository.Filters.withFspCode(input.fspCode())).isPresent()) {

            LOGGER.info("FSP with FSP Code {} already exists", input.fspCode().getFspCode());
            throw new FspAlreadyExistsException("FSP Code", input.fspCode().getFspCode());
        }

        var fsp = new Fsp(input.fspCode(), input.name());
        LOGGER.info("Created FSP: {}", fsp);

        for (var currency : input.supportedCurrencies()) {

            LOGGER.info("Adding supported currency: {}", currency);
            fsp.addSupportedCurrency(currency);
            LOGGER.info("Added supported currency: {}", currency);
        }

        for (var endpoint : input.endpoints()) {

            LOGGER.info("Adding endpoint: {}", endpoint);
            fsp.addEndpoint(endpoint.type(), endpoint.host());
            LOGGER.info("Added endpoint: {}", endpoint);
        }

        this.fspRepository.save(fsp);

        LOGGER.info("Completed CreateFspCommand with input: {}", input);

        return new Output(fsp.getId());
    }

}

package io.mojaloop.core.wallet.domain.command.position;

import io.mojaloop.component.jpa.routing.annotation.Write;
import io.mojaloop.component.misc.logger.ObjectLogger;
import io.mojaloop.core.wallet.contract.command.position.UpdateNetDebitCapCommand;
import io.mojaloop.core.wallet.contract.exception.position.FailedToUpdateNdcException;
import io.mojaloop.core.wallet.contract.exception.position.PositionIdNotFoundException;
import io.mojaloop.core.wallet.domain.repository.PositionRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class UpdateNetDebitCapCommandHandler implements UpdateNetDebitCapCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        UpdateNetDebitCapCommandHandler.class);

    private final PositionRepository positionRepository;

    public UpdateNetDebitCapCommandHandler(PositionRepository positionRepository) {

        assert positionRepository != null;

        this.positionRepository = positionRepository;
    }

    @Transactional
    @Write
    @Override
    public Output execute(Input input) throws FailedToUpdateNdcException {

        LOGGER.info("UpdateNetDebitCapCommand : input: ({})", ObjectLogger.log(input));

        var position = this.positionRepository
                           .selectForUpdate(input.positionId())
                           .orElseThrow(() -> new PositionIdNotFoundException(input.positionId()));

        position.updateNdc(input.netDebitCap());

        this.positionRepository.save(position);

        var output = new Output(position.getPosition(), position.getReserved(), position.getNdc());

        LOGGER.info("UpdateNetDebitCapCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

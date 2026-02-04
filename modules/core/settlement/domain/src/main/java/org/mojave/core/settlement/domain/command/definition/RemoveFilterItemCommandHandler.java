package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.RemoveFilterItemCommand;
import org.mojave.core.settlement.contract.exception.FilterGroupIdNotFoundException;
import org.mojave.core.settlement.domain.repository.FilterGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class RemoveFilterItemCommandHandler implements RemoveFilterItemCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        RemoveFilterItemCommandHandler.class);

    private final FilterGroupRepository filterGroupRepository;

    public RemoveFilterItemCommandHandler(final FilterGroupRepository filterGroupRepository) {

        Objects.requireNonNull(filterGroupRepository);
        this.filterGroupRepository = filterGroupRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("RemoveFilterItemCommand : input: ({})", ObjectLogger.log(input));

        var group = this.filterGroupRepository
                        .findById(input.filterGroupId())
                        .orElseThrow(
                            () -> new FilterGroupIdNotFoundException(input.filterGroupId()));

        group.removeItem(input.filterItemId());
        this.filterGroupRepository.save(group);

        var output = new Output(group.getId());

        LOGGER.info("RemoveFilterItemCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

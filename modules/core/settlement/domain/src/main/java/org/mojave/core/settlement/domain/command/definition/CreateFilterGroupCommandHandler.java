package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.CreateFilterGroupCommand;
import org.mojave.core.settlement.contract.exception.FilterGroupNameAlreadyExistsException;
import org.mojave.core.settlement.domain.model.FilterGroup;
import org.mojave.core.settlement.domain.repository.FilterGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class CreateFilterGroupCommandHandler implements CreateFilterGroupCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(
        CreateFilterGroupCommandHandler.class);

    private final FilterGroupRepository filterGroupRepository;

    public CreateFilterGroupCommandHandler(final FilterGroupRepository filterGroupRepository) {

        Objects.requireNonNull(filterGroupRepository);
        this.filterGroupRepository = filterGroupRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("CreateFilterGroupCommand : input: ({})", ObjectLogger.log(input));

        if (this.filterGroupRepository
                .findOne(FilterGroupRepository.Filters.withNameEquals(input.name()))
                .isPresent()) {
            throw new FilterGroupNameAlreadyExistsException(input.name());
        }

        var group = new FilterGroup(input.name());
        group = this.filterGroupRepository.save(group);

        var output = new Output(group.getId());

        LOGGER.info("CreateFilterGroupCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

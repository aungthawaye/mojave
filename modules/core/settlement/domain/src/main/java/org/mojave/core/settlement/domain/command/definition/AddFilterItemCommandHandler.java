package org.mojave.core.settlement.domain.command.definition;

import org.mojave.component.jpa.routing.annotation.Write;
import org.mojave.component.misc.logger.ObjectLogger;
import org.mojave.core.settlement.contract.command.definition.AddFilterItemCommand;
import org.mojave.core.settlement.contract.exception.FilterGroupIdNotFoundException;
import org.mojave.core.settlement.domain.repository.FilterGroupRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Objects;

@Service
public class AddFilterItemCommandHandler implements AddFilterItemCommand {

    private static final Logger LOGGER = LoggerFactory.getLogger(AddFilterItemCommandHandler.class);

    private final FilterGroupRepository filterGroupRepository;

    public AddFilterItemCommandHandler(final FilterGroupRepository filterGroupRepository) {

        Objects.requireNonNull(filterGroupRepository);
        this.filterGroupRepository = filterGroupRepository;
    }

    @Override
    @Transactional
    @Write
    public Output execute(final Input input) {

        LOGGER.info("AddFilterItemCommand : input: ({})", ObjectLogger.log(input));

        var group = this.filterGroupRepository
                        .findById(input.filterGroupId())
                        .orElseThrow(
                            () -> new FilterGroupIdNotFoundException(input.filterGroupId()));

        var item = group.addItem(input.fspId());
        this.filterGroupRepository.save(group);

        var output = new Output(item.getId());

        LOGGER.info("AddFilterItemCommand : output : ({})", ObjectLogger.log(output));

        return output;
    }

}

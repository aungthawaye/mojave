package org.mojave.core.participant.domain.query;

import org.mojave.component.jpa.routing.annotation.Read;
import org.mojave.core.common.datatype.enums.ActivationStatus;
import org.mojave.core.common.datatype.enums.TerminationStatus;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.core.participant.contract.exception.ssp.SspCodeNotFoundException;
import org.mojave.core.participant.contract.exception.ssp.SspIdNotFoundException;
import org.mojave.core.participant.contract.query.SspQuery;
import org.mojave.core.participant.domain.model.ssp.Ssp;
import org.mojave.core.participant.domain.repository.SspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Primary;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@Primary
public class SspQueryHandler implements SspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(SspQueryHandler.class);

    private final SspRepository sspRepository;

    public SspQueryHandler(final SspRepository sspRepository) {

        assert sspRepository != null;

        this.sspRepository = sspRepository;
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SspData get(final SspId sspId) {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository.findOne(activationStatus.and(terminationStatus)).orElseThrow(
            () -> new SspIdNotFoundException(sspId)).convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public SspData get(final SspCode sspCode) {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository
                   .findOne(activationStatus
                                .and(terminationStatus)
                                .and(SspRepository.Filters.withSspCode(sspCode)))
                   .orElseThrow(() -> new SspCodeNotFoundException(sspCode))
                   .convert();
    }

    @Transactional(readOnly = true)
    @Read
    @Override
    public List<SspData> getAll() {

        var activationStatus = SspRepository.Filters.withActivationStatus(ActivationStatus.ACTIVE);
        var terminationStatus = SspRepository.Filters.withTerminationStatus(
            TerminationStatus.ALIVE);

        return this.sspRepository.findAll(activationStatus.and(terminationStatus)).stream().map(
            Ssp::convert).toList();
    }
}

package io.mojaloop.core.participant.domain.query;

import io.mojaloop.core.common.datatype.identifier.participant.FspId;
import io.mojaloop.core.common.datatype.type.fspiop.FspCode;
import io.mojaloop.core.participant.contract.data.FspData;
import io.mojaloop.core.participant.contract.query.FspQuery;
import io.mojaloop.core.participant.domain.model.Fsp;
import io.mojaloop.core.participant.domain.model.repository.FspRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class FspQueryHandler implements FspQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(FspQueryHandler.class);

    private final FspRepository fspRepository;

    public FspQueryHandler(FspRepository fspRepository) {

        assert fspRepository != null;

        this.fspRepository = fspRepository;
    }

    @Override
    public FspData get(FspId fspId) {

        return this.fspRepository.getReferenceById(fspId).convert();
    }

    @Override
    public FspData get(FspCode fspCode) {

        return this.fspRepository.findOne(FspRepository.Filters.withFspCode(fspCode)).orElseThrow().convert();
    }

    @Override
    public List<FspData> getAll() {

        return this.fspRepository.findAll().stream().map(Fsp::convert).toList();
    }

}

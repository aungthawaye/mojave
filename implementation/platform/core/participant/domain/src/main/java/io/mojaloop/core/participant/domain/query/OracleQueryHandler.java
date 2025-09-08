package io.mojaloop.core.participant.domain.query;

import io.mojaloop.core.common.datatype.identifier.participant.OracleId;
import io.mojaloop.core.participant.contract.data.OracleData;
import io.mojaloop.core.participant.contract.exception.OracleIdNotFoundException;
import io.mojaloop.core.participant.contract.exception.OracleTypeNotFoundException;
import io.mojaloop.core.participant.contract.query.OracleQuery;
import io.mojaloop.core.participant.domain.model.oracle.Oracle;
import io.mojaloop.core.participant.domain.repository.OracleRepository;
import io.mojaloop.fspiop.spec.core.PartyIdType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class OracleQueryHandler implements OracleQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(OracleQueryHandler.class);

    private final OracleRepository oracleRepository;

    public OracleQueryHandler(OracleRepository oracleRepository) {

        assert oracleRepository != null;

        this.oracleRepository = oracleRepository;
    }

    @Override
    public Optional<OracleData> find(PartyIdType type) {

        var existing = this.oracleRepository.findOne(OracleRepository.Filters.withType(type));

        if (existing.isEmpty()) {
            LOGGER.debug("No oracle found for type: {}", type);
            return Optional.empty();
        }

        return Optional.of(existing.get().convert());
    }

    @Override
    public OracleData get(PartyIdType type) throws OracleTypeNotFoundException {

        return this.oracleRepository.findOne(OracleRepository.Filters.withType(type)).orElseThrow(() -> new OracleTypeNotFoundException(type)).convert();
    }

    @Override
    public OracleData get(OracleId oracleId) throws OracleIdNotFoundException {

        return this.oracleRepository.findById(oracleId).orElseThrow(() -> new OracleIdNotFoundException(oracleId)).convert();
    }

    @Override
    public List<OracleData> getAll() {

        return this.oracleRepository.findAll().stream().map(Oracle::convert).toList();
    }

}

package io.mojaloop.core.participant.domain.query;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.exception.hub.HubIdNotFoundException;
import io.mojaloop.core.participant.contract.query.HubQuery;
import io.mojaloop.core.participant.domain.model.hub.Hub;
import io.mojaloop.core.participant.domain.repository.HubRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class HubQueryHandler implements HubQuery {

    private static final Logger LOGGER = LoggerFactory.getLogger(HubQueryHandler.class);

    private final HubRepository hubRepository;

    public HubQueryHandler(HubRepository hubRepository) {

        assert hubRepository != null;

        this.hubRepository = hubRepository;
    }

    @Override
    public HubData get(HubId hubId) throws HubIdNotFoundException {

        return this.hubRepository.findById(hubId).orElseThrow(() -> new HubIdNotFoundException(hubId)).convert();
    }

    @Override
    public List<HubData> getAll() {

        return this.hubRepository.findAll().stream().map(Hub::convert).toList();
    }

    @Override
    public long count() {

        return this.hubRepository.count();
    }
}

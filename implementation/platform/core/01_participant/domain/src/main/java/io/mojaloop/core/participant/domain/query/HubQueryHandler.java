/*-
 * ================================================================================
 * Mojaloop OSS
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ================================================================================
 */

package io.mojaloop.core.participant.domain.query;

import io.mojaloop.core.common.datatype.identifier.participant.HubId;
import io.mojaloop.core.participant.contract.data.HubData;
import io.mojaloop.core.participant.contract.exception.hub.HubNotFoundException;
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
    public long count() {

        return this.hubRepository.count();
    }

    @Override
    public HubData get() {

        return this.hubRepository.findById(new HubId()).orElseThrow(HubNotFoundException::new).convert();
    }

    @Override
    public List<HubData> getAll() {

        return this.hubRepository.findAll().stream().map(Hub::convert).toList();
    }

}

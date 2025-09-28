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

package io.mojaloop.core.participant.domain.cache.redis.updater;

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.participant.domain.cache.ParticipantCache;
import io.mojaloop.core.participant.domain.model.fsp.Fsp;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class FspCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(Fsp fsp) {

        var participantCache = SpringContext.getBean(ParticipantCache.class, ParticipantCache.Qualifiers.DEFAULT);

        participantCache.save(fsp.convert());
    }

    @PostRemove
    public void postRemove(Fsp fsp) {

        var participantCache = SpringContext.getBean(ParticipantCache.class, ParticipantCache.Qualifiers.DEFAULT);

        participantCache.delete(fsp.getId());
    }

}

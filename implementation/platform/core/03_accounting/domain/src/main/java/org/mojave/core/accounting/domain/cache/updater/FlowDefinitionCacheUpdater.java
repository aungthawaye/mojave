/*-
 * ================================================================================
 * Mojave
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

package org.mojave.core.accounting.domain.cache.updater;

import org.mojave.component.misc.spring.SpringContext;
import org.mojave.core.accounting.domain.cache.FlowDefinitionCache;
import org.mojave.core.accounting.domain.model.FlowDefinition;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class FlowDefinitionCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(final FlowDefinition flowDefinition) {

        final var cache = SpringContext.getBean(FlowDefinitionCache.class);
        cache.save(flowDefinition.convert());
    }

    @PostRemove
    public void postRemove(final FlowDefinition flowDefinition) {

        final var cache = SpringContext.getBean(FlowDefinitionCache.class);
        cache.delete(flowDefinition.getId());
    }

}

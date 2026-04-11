/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
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
 * ===
 */

package org.mojave.accounting.domain.cache.updater;

import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;
import org.mojave.component.misc.spring.SpringContext;
import org.mojave.accounting.domain.cache.CoaEntryCache;
import org.mojave.accounting.domain.model.CoaEntry;

public class CoaEntryCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(CoaEntry entry) {

        var cache = SpringContext.getBean(CoaEntryCache.class);

        cache.save(entry.convert());
    }

    @PostRemove
    public void postRemove(CoaEntry entry) {

        var cache = SpringContext.getBean(CoaEntryCache.class);

        cache.delete(entry.getId());
    }

}

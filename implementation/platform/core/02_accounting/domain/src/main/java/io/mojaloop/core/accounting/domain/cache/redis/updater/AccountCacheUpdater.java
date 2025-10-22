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
package io.mojaloop.core.accounting.domain.cache.redis.updater;

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.accounting.domain.cache.AccountCache;
import io.mojaloop.core.accounting.domain.model.Account;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class AccountCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(Account account) {

        var accountCache = SpringContext.getBean(AccountCache.class);

        accountCache.save(account.convert());
    }

    @PostRemove
    public void postRemove(Account account) {

        var accountCache = SpringContext.getBean(AccountCache.class);

        accountCache.delete(account.getId());
    }

}

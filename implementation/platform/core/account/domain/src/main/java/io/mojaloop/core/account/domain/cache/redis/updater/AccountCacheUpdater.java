package io.mojaloop.core.account.domain.cache.redis.updater;

import io.mojaloop.component.misc.spring.SpringContext;
import io.mojaloop.core.account.domain.cache.AccountCache;
import io.mojaloop.core.account.domain.model.Account;
import jakarta.persistence.PostPersist;
import jakarta.persistence.PostRemove;
import jakarta.persistence.PostUpdate;

public class AccountCacheUpdater {

    @PostPersist
    @PostUpdate
    public void persistOrUpdate(Account account) {

        var accountCache = SpringContext.getBean(AccountCache.class, AccountCache.Qualifiers.DEFAULT);

        accountCache.save(account.convert());
    }

    @PostRemove
    public void postRemove(Account account) {

        var accountCache = SpringContext.getBean(AccountCache.class, AccountCache.Qualifiers.DEFAULT);

        accountCache.delete(account.getId());
    }

}

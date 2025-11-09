package io.mojaloop.core.accounting.store;

import io.mojaloop.core.accounting.intercom.client.service.AccountingIntercomService;

class AccountingStoreSettings implements AccountingStoreConfiguration.RequiredSettings {

    @Override
    public AccountingIntercomService.Settings accountingIntercomServiceSettings() {

        return null;
    }

    @Override
    public AccountingStoreConfiguration.Settings accountingStoreSettings() {

        return null;
    }
}

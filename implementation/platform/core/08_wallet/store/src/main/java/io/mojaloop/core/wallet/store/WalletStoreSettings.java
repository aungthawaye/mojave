package io.mojaloop.core.wallet.store;

import io.mojaloop.core.wallet.intercom.client.service.WalletIntercomService;

class WalletStoreSettings implements WalletStoreConfiguration.RequiredSettings {

    @Override
    public WalletIntercomService.Settings walletIntercomServiceSettings() {

        return null;
    }

    @Override
    public WalletStoreConfiguration.Settings walletStoreSettings() {

        return null;
    }
}

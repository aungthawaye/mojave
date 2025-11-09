package io.mojaloop.core.wallet.store;

import io.mojaloop.core.wallet.intercom.client.WalletIntercomClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {WalletIntercomClientConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.wallet.store"})
public class WalletStoreConfiguration implements WalletIntercomClientConfiguration.RequiredBeans {

    public interface RequiredBeans extends WalletIntercomClientConfiguration.RequiredBeans { }

    public interface RequiredSettings extends WalletIntercomClientConfiguration.RequiredSettings {

        WalletStoreConfiguration.Settings walletStoreSettings();

    }

    public record Settings(int refreshIntervalMs) { }
}

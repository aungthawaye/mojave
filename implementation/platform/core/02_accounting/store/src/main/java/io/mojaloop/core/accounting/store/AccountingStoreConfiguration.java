package io.mojaloop.core.accounting.store;

import io.mojaloop.core.accounting.intercom.client.AccountingIntercomClientConfiguration;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;

@Import(value = {AccountingIntercomClientConfiguration.class})
@ComponentScan(basePackages = {"io.mojaloop.core.accounting.store"})
public class AccountingStoreConfiguration implements AccountingIntercomClientConfiguration.RequiredBeans {

    public interface RequiredBeans extends AccountingIntercomClientConfiguration.RequiredBeans { }

    public interface RequiredSettings extends AccountingIntercomClientConfiguration.RequiredSettings {

        AccountingStoreConfiguration.Settings accountingStoreSettings();

    }

    public record Settings(int refreshIntervalMs) { }
}

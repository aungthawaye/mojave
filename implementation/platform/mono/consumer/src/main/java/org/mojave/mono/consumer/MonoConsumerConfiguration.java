package org.mojave.mono.consumer;

import org.mojave.core.accounting.consumer.AccountingConsumerConfiguration;
import org.mojave.core.transaction.consumer.TransactionConsumerConfiguration;
import org.springframework.context.annotation.Import;

@Import(
    value = {
        TransactionConsumerConfiguration.class,
        AccountingConsumerConfiguration.class})
public class MonoConsumerConfiguration {

    public interface RequiredDependencies
        extends TransactionConsumerConfiguration.RequiredDependencies,
                AccountingConsumerConfiguration.RequiredDependencies { }

    public interface RequiredSettings extends TransactionConsumerConfiguration.RequiredSettings,
                                              AccountingConsumerConfiguration.RequiredSettings { }

}

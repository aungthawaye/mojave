package org.mojave.core.accounting.intercom.producer;

import org.springframework.context.annotation.Import;

@Import(
    value = {
        AccountingIntercomProducerConfiguration.class,
        AccountingIntercomProducerSettings.class})
public class AccountingIntercomProducerTestConfiguration { }

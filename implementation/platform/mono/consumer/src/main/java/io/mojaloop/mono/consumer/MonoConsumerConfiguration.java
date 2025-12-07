package io.mojaloop.mono.consumer;

import io.mojaloop.core.accounting.consumer.AccountingConsumerConfiguration;
import io.mojaloop.core.transaction.consumer.TransactionConsumerConfiguration;
import org.springframework.context.annotation.Import;

@Import(value = {TransactionConsumerConfiguration.class, AccountingConsumerConfiguration.class})
public class MonoConsumerConfiguration { }

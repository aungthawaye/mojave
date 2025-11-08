package io.mojaloop.core.transaction.consumer;

import org.springframework.context.annotation.Import;

@Import(value = {TransactionConsumerConfiguration.class, TestSettings.class})
public class TestConfiguration { }

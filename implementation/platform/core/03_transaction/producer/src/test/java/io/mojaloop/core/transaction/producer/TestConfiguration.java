package io.mojaloop.core.transaction.producer;

import org.springframework.context.annotation.Import;

@Import(value = {TransactionProducerConfiguration.class, TestSettings.class})
public class TestConfiguration { }

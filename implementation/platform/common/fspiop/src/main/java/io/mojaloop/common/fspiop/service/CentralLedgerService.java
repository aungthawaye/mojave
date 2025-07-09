package io.mojaloop.common.fspiop.service;

public interface CentralLedgerService {

    record Settings(String baseUrl, int port) { }

}

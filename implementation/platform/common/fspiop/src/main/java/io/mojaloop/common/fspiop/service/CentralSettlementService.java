package io.mojaloop.common.fspiop.service;

public interface CentralSettlementService {

    record Settings(String baseUrl, int port) { }

}

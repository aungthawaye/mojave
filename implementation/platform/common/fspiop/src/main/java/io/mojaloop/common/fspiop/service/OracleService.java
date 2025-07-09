package io.mojaloop.common.fspiop.service;

public interface OracleService {

    record Settings(String baseUrl, int port) { }

}

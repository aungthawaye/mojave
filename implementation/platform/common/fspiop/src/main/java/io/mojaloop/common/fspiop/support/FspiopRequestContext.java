package io.mojaloop.common.fspiop.support;

import java.util.Map;

public record FspiopRequestContext(String uri, Map<String, String> headers, String payload) {

    public boolean hasPayload() {

        if (this.payload == null) {
            return false;
        }

        return !this.payload.isEmpty();
    }

}

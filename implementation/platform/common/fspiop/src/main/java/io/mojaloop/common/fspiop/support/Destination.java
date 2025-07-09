package io.mojaloop.common.fspiop.support;

public record Destination(String destinationFspId) {

    public static Destination EMPTY() {

        return new Destination(null);
    }

}

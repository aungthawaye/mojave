package io.mojaloop.common.fspiop.support;

public record Source(String destinationFspId) {

    public static Source EMPTY() {

        return new Source(null);
    }

}

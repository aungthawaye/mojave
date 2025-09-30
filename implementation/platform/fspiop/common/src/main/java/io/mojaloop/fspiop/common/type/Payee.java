package io.mojaloop.fspiop.common.type;

public record Payee(String fspCode) {

    public Payee {

        if (fspCode == null) {
            throw new IllegalArgumentException("Payee FSP code cannot be null");
        }

    }

    public static Payee EMPTY() {

        return new Payee("");
    }

    public boolean isEmpty() {

        return this.fspCode.isEmpty();
    }

    @Override
    public String toString() {

        return this.fspCode;
    }

}

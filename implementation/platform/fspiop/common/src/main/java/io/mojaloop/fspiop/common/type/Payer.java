package io.mojaloop.fspiop.common.type;

public record Payer(String fspCode) {

    public Payer {

        if (fspCode == null) {
            throw new IllegalArgumentException("Payer FSP code cannot be null");
        }

    }

    public static Payer EMPTY() {

        return new Payer("");
    }

    public static Payer of(String fspCode) {

        return fspCode != null ? new Payer(fspCode) : EMPTY();
    }

    public boolean isEmpty() {

        return this.fspCode.isEmpty();
    }

    @Override
    public String toString() {

        return this.fspCode;
    }

}

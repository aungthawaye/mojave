package io.mojaloop.core.common.datatype.type.accounting;

import lombok.Getter;

@Getter
public class AccountResolving {

    private final Type type;

    private final Long resolvingId;

    private final String participant;

    private AccountResolving(Type type, Long resolvingId, String participant) {

        this.type = type;
        this.resolvingId = resolvingId;
        this.participant = participant;
    }

    public static AccountResolving byAccount(Long resolvingId) {

        return new AccountResolving(Type.ACCOUNT, resolvingId, null);
    }

    public static AccountResolving byChartEntry(Long resolvingId, String participant) {

        return new AccountResolving(Type.CHART_ENTRY, resolvingId, participant);
    }

    public enum Type {
        ACCOUNT,
        CHART_ENTRY
    }

}

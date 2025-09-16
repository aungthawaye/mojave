package io.mojaloop.core.account.contract.data;

import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;

import java.time.Instant;
import java.util.Objects;

public record ChartEntryData(ChartEntryId chartEntryId,
                             ChartEntryCode code,
                             String name,
                             String description,
                             AccountType accountType,
                             Instant createdAt,
                             ChartId chartId) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ChartEntryData that)) {
            return false;
        }
        return Objects.equals(chartEntryId, that.chartEntryId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(chartEntryId);
    }

}
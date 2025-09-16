package io.mojaloop.core.account.contract.data;

import io.mojaloop.core.common.datatype.identifier.account.ChartId;

import java.time.Instant;
import java.util.Objects;

public record ChartData(ChartId chartId, String name, Instant createdAt, ChartEntryData[] entries) {

    @Override
    public boolean equals(Object o) {

        if (!(o instanceof ChartData that)) {
            return false;
        }
        return Objects.equals(chartId, that.chartId);
    }

    @Override
    public int hashCode() {

        return Objects.hashCode(chartId);
    }

}
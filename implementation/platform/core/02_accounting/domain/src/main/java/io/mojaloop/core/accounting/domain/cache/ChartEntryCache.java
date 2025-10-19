package io.mojaloop.core.accounting.domain.cache;

import io.mojaloop.core.accounting.contract.data.ChartEntryData;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.accounting.ChartId;
import io.mojaloop.core.common.datatype.type.accounting.ChartEntryCode;

import java.util.Set;

public interface ChartEntryCache {

    void clear();

    void delete(ChartEntryId chartEntryId);

    ChartEntryData get(ChartEntryId chartEntryId);

    ChartEntryData get(ChartEntryCode code);

    Set<ChartEntryData> get(ChartId chartId);

    void save(ChartEntryData chartEntry);

    class Qualifiers {

        public static final String REDIS = "redis";

        public static final String IN_MEMORY = "in-memory";

        public static final String DEFAULT = REDIS;

    }

    class Names {

        public static final String WITH_ID = "acc-chart-entry-with-id";

        public static final String WITH_CODE = "acc-chart-entry-with-code";

    }

}

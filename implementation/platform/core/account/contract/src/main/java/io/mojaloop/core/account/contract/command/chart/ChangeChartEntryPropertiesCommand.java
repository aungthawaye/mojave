package io.mojaloop.core.account.contract.command.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.account.contract.exception.chart.ChartEntryIdNotFoundException;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface ChangeChartEntryPropertiesCommand {

    Output execute(Input input) throws ChartEntryIdNotFoundException;

    record Input(
        @JsonProperty(required = true) @NotNull ChartEntryId chartEntryId,
        @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
        @JsonProperty(required = false) @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description
    ) { }

    record Output(ChartEntryId chartEntryId) { }

}

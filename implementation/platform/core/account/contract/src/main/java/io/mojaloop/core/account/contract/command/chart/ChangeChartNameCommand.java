package io.mojaloop.core.account.contract.command.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.account.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface ChangeChartNameCommand {

    Output execute(Input input) throws ChartIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull ChartId chartId,
                 @JsonProperty(required = true) @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name) { }

    record Output(ChartId chartId) { }

}

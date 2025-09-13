package io.mojaloop.core.account.contract.command.chart;

import com.fasterxml.jackson.annotation.JsonProperty;
import io.mojaloop.component.misc.constraint.StringSizeConstraints;
import io.mojaloop.core.account.contract.exception.chart.ChartIdNotFoundException;
import io.mojaloop.core.common.datatype.enums.account.AccountType;
import io.mojaloop.core.common.datatype.identifier.account.ChartEntryId;
import io.mojaloop.core.common.datatype.identifier.account.ChartId;
import io.mojaloop.core.common.datatype.type.account.ChartEntryCode;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public interface CreateChartEntryCommand {

    Output execute(Input input) throws ChartIdNotFoundException;

    record Input(@JsonProperty(required = true) @NotNull ChartId chartId,
                 @JsonProperty(required = true) @NotNull ChartEntryCode code,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_NAME_TITLE_LENGTH) String name,
                 @JsonProperty(required = true) @NotNull @NotBlank @Size(max = StringSizeConstraints.MAX_DESCRIPTION_LENGTH) String description,
                 @JsonProperty(required = true) @NotNull AccountType accountType) { }

    record Output(ChartEntryId chartEntryId) { }

}

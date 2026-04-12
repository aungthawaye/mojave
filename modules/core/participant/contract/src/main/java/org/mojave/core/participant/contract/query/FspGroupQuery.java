package org.mojave.core.participant.contract.query;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotNull;
import org.mojave.scheme.rule.identifier.participant.FspGroupId;
import org.mojave.core.participant.contract.data.FspGroupData;

import java.util.List;

public interface FspGroupQuery {

    String GET_BY_ID_SUBJECT_NAME = "sub-participant.fsp-group-query.get-by-id";

    String GET_ALL_SUBJECT_NAME = "sub-participant.fsp-group-query.get-all";

    FspGroupData get(FspGroupId fspGroupId);

    List<FspGroupData> getAll();

    record GetByIdInput(@JsonProperty(required = true) @NotNull FspGroupId fspGroupId) { }

    record GetAllInput() { }

}

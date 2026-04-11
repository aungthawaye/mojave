package org.mojave.participant.contract.query;

import org.mojave.common.datatype.identifier.participant.FspGroupId;
import org.mojave.participant.contract.data.FspGroupData;

import java.util.List;

public interface FspGroupQuery {

    FspGroupData get(FspGroupId fspGroupId);

    List<FspGroupData> getAll();

}

package org.mojave.core.participant.contract.query;

import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.SspData;

import java.util.List;

public interface SspQuery {

    SspData get(SspId sspId);

    SspData get(SspCode sspCode);

    List<SspData> getAll();

}

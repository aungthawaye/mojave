/*-
 * ===
 * Mojave
 * ---
 * Copyright (C) 2025 Open Source
 * ---
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *      http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * ===
 */
package org.mojave.core.participant.store;

import org.mojave.core.common.datatype.identifier.participant.FspId;
import org.mojave.core.common.datatype.identifier.participant.OracleId;
import org.mojave.core.common.datatype.identifier.participant.SspId;
import org.mojave.core.common.datatype.type.participant.FspCode;
import org.mojave.core.common.datatype.type.participant.SspCode;
import org.mojave.core.participant.contract.data.FspData;
import org.mojave.core.participant.contract.data.OracleData;
import org.mojave.core.participant.contract.data.SspData;
import org.mojave.scheme.fspiop.core.PartyIdType;

public interface ParticipantStore {

    FspData getFspData(FspId fspId);

    FspData getFspData(FspCode fspCode);

    SspData getSspData(SspId sspId);

    SspData getSspData(SspCode sspCode);

    OracleData getOracleData(OracleId oracleId);

    OracleData getOracleData(PartyIdType partyIdType);

}

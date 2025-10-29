/*-
 * ================================================================================
 * Mojave
 * --------------------------------------------------------------------------------
 * Copyright (C) 2025 Open Source
 * --------------------------------------------------------------------------------
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
 * ================================================================================
 */
package io.mojaloop.core.common.datatype.type.accounting;

import lombok.Getter;

@Getter
public class AccountResolving {

    private final Type type;

    private final Long resolvingId;

    private final String participant;

    private AccountResolving(Type type, Long resolvingId, String participant) {

        this.type = type;
        this.resolvingId = resolvingId;
        this.participant = participant;
    }

    public static AccountResolving byAccount(Long resolvingId) {

        return new AccountResolving(Type.ACCOUNT, resolvingId, null);
    }

    public static AccountResolving byChartEntry(Long resolvingId, String participant) {

        return new AccountResolving(Type.CHART_ENTRY, resolvingId, participant);
    }

    public enum Type {
        ACCOUNT,
        CHART_ENTRY
    }

}

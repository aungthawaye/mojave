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
package org.mojave.fspiop.client.api.parties;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mojave.fspiop.client.api.TestSettings;
import org.mojave.fspiop.component.exception.FspiopException;
import org.mojave.fspiop.component.type.Payee;
import org.mojave.fspiop.invoker.FspiopInvokerConfiguration;
import org.mojave.fspiop.invoker.api.parties.GetParties;
import org.mojave.fspiop.spec.core.PartyIdType;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith(SpringExtension.class)
@ContextConfiguration(
    classes = {
        FspiopInvokerConfiguration.class,
        TestSettings.class})
public class GetPartiesUT {

    @Autowired
    GetParties getParties;

    @Test
    public void test() throws FspiopException {

        this.getParties.getParties(new Payee("fsp2"), PartyIdType.MSISDN, "987654321");
    }

}

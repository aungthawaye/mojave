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

package org.mojave.fspiop.component.interledger;

import com.google.common.primitives.UnsignedLong;
import org.junit.jupiter.api.Test;

public class InterledgerUT {

    @Test
    public void test_unwrap() {

        var prepare = Interledger.prepare(
            "53cr3t", Interledger.address("test"), UnsignedLong.valueOf(100), "data", 100);

        System.out.println("package : " + prepare.base64PreparePacket());
        System.out.println("fulfillment : " + prepare.base64Fulfillment());
        System.out.println("condition : " + prepare.base64Condition());

        var unwrapped = Interledger.unwrap(
            "AYIDDwAAAAAAAACWHmcud2FsbGV0MS5tc2lzZG4uMTAwOTg4Mzg3NzE0MoIC5GV5SnhkVzkwWlVsa0lqb2lNREZMT0ZjMVFWazVWazB5V2tzNFUwSXhTa05hU2xvMFN6TWlMQ0owY21GdWMyRmpkR2x2Ymtsa0lqb2lNREZMT0ZjMVFUQmFORmRGVkZNM1VWbFJWMVpDTWpoV05qZ2lMQ0owY21GdWMyRmpkR2x2YmxSNWNHVWlPbnNpYzJObGJtRnlhVzhpT2lKVVVrRk9VMFpGVWlJc0luTjFZbE5qWlc1aGNtbHZJam9pVUVWU1UwOU9YMVJQWDFCRlVsTlBUaUlzSW1sdWFYUnBZWFJ2Y2lJNklsQkJXVVZTSWl3aWFXNXBkR2xoZEc5eVZIbHdaU0k2SWtOUFRsTlZUVVZTSW4wc0luQmhlV1ZsSWpwN0luQmhjblI1U1dSSmJtWnZJanA3SW5CaGNuUjVTV1JVZVhCbElqb2lUVk5KVTBST0lpd2ljR0Z5ZEhsSlpHVnVkR2xtYVdWeUlqb2lNVEF3T1RnNE16ZzNOekUwTWlJc0ltWnpjRWxrSWpvaWQyRnNiR1YwTVNKOUxDSndaWEp6YjI1aGJFbHVabThpT25zaVkyOXRjR3hsZUU1aGJXVWlPbnNpWm1seWMzUk9ZVzFsSWpvaVRXOGlMQ0pzWVhOMFRtRnRaU0k2SWs1cEluMTlmU3dpY0dGNVpYSWlPbnNpY0dGeWRIbEpaRWx1Wm04aU9uc2ljR0Z5ZEhsSlpGUjVjR1VpT2lKTlUwbFRSRTRpTENKd1lYSjBlVWxrWlc1MGFXWnBaWElpT2lJNU9EZ3pPRGMzTVRReklpd2labk53U1dRaU9pSjNZV3hzWlhReUluMTlMQ0psZUhCcGNtRjBhVzl1SWpvaU1qQXlOUzB4TUMwek1WUXdNem96TmpveE55NHdOVEZhSWl3aVlXMXZkVzUwSWpwN0ltRnRiM1Z1ZENJNklqRXVOU0lzSW1OMWNuSmxibU41SWpvaVRGSkVJbjE5AA");

        System.out.println("unwrapped condition : " + unwrapped.getExecutionCondition().getHash());
        System.out.println("unwrapped data : " + new String(unwrapped.getData()));
        System.out.println("unwrapped amount : " + unwrapped.getAmount());
        System.out.println("unwrapped destination : " + unwrapped.getDestination());
        System.out.println("unwrapped expiration : " + unwrapped.getExpiresAt());
    }

}

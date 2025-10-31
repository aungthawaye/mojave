package io.mojaloop.fspiop.component.interledger;

import com.google.common.primitives.UnsignedLong;
import org.interledger.core.InterledgerAddress;
import org.junit.jupiter.api.Test;

public class InterledgerUT {

    @Test
    public void test_unwrap() {

        var prepare = Interledger.prepare("53cr3t", Interledger.address("test"), UnsignedLong.valueOf(100), "data", 100);

        System.out.println("package : " + prepare.base64PreparePacket());
        System.out.println("fulfillment : " + prepare.base64Fulfillment());
        System.out.println("condition : " + prepare.base64Condition());

        var unwrapped = Interledger.unwrap("AYIDDwAAAAAAAACWHmcud2FsbGV0MS5tc2lzZG4uMTAwOTg4Mzg3NzE0MoIC5GV5SnhkVzkwWlVsa0lqb2lNREZMT0ZjMVFWazVWazB5V2tzNFUwSXhTa05hU2xvMFN6TWlMQ0owY21GdWMyRmpkR2x2Ymtsa0lqb2lNREZMT0ZjMVFUQmFORmRGVkZNM1VWbFJWMVpDTWpoV05qZ2lMQ0owY21GdWMyRmpkR2x2YmxSNWNHVWlPbnNpYzJObGJtRnlhVzhpT2lKVVVrRk9VMFpGVWlJc0luTjFZbE5qWlc1aGNtbHZJam9pVUVWU1UwOU9YMVJQWDFCRlVsTlBUaUlzSW1sdWFYUnBZWFJ2Y2lJNklsQkJXVVZTSWl3aWFXNXBkR2xoZEc5eVZIbHdaU0k2SWtOUFRsTlZUVVZTSW4wc0luQmhlV1ZsSWpwN0luQmhjblI1U1dSSmJtWnZJanA3SW5CaGNuUjVTV1JVZVhCbElqb2lUVk5KVTBST0lpd2ljR0Z5ZEhsSlpHVnVkR2xtYVdWeUlqb2lNVEF3T1RnNE16ZzNOekUwTWlJc0ltWnpjRWxrSWpvaWQyRnNiR1YwTVNKOUxDSndaWEp6YjI1aGJFbHVabThpT25zaVkyOXRjR3hsZUU1aGJXVWlPbnNpWm1seWMzUk9ZVzFsSWpvaVRXOGlMQ0pzWVhOMFRtRnRaU0k2SWs1cEluMTlmU3dpY0dGNVpYSWlPbnNpY0dGeWRIbEpaRWx1Wm04aU9uc2ljR0Z5ZEhsSlpGUjVjR1VpT2lKTlUwbFRSRTRpTENKd1lYSjBlVWxrWlc1MGFXWnBaWElpT2lJNU9EZ3pPRGMzTVRReklpd2labk53U1dRaU9pSjNZV3hzWlhReUluMTlMQ0psZUhCcGNtRjBhVzl1SWpvaU1qQXlOUzB4TUMwek1WUXdNem96TmpveE55NHdOVEZhSWl3aVlXMXZkVzUwSWpwN0ltRnRiM1Z1ZENJNklqRXVOU0lzSW1OMWNuSmxibU41SWpvaVRGSkVJbjE5AA");

        System.out.println("unwrapped condition : " + unwrapped.getExecutionCondition().getHash());
        System.out.println("unwrapped data : " + new String(unwrapped.getData()));
        System.out.println("unwrapped amount : " + unwrapped.getAmount());
        System.out.println("unwrapped destination : " + unwrapped.getDestination());
        System.out.println("unwrapped expiration : " + unwrapped.getExpiresAt());
    }

}

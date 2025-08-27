/*-
 * ================================================================================
 * Mojaloop OSS
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

package io.mojaloop.fspiop.client.api;

import io.mojaloop.fspiop.common.FspiopCommonConfiguration;
import io.mojaloop.fspiop.invoker.FspiopInvokerConfiguration;
import io.mojaloop.fspiop.invoker.api.PartiesService;
import io.mojaloop.fspiop.invoker.api.QuotesService;
import io.mojaloop.fspiop.invoker.api.TransfersService;
import org.springframework.context.annotation.Bean;

import java.util.Map;

public class TestSettings implements FspiopInvokerConfiguration.RequiredSettings {

    @Bean
    @Override
    public FspiopCommonConfiguration.ParticipantSettings fspiopParticipantSettings() {

        return new FspiopCommonConfiguration.ParticipantSettings("fsp1",
                                                                 "FSP 1",
                                                                 "This_is_ILP_$ecret",
                                                                 true,
                                                                 true,
                                                                 "MIIEvgIBADANBgkqhkiG9w0BAQEFAASCBKgwggSkAgEAAoIBAQCySUm2YlgrOwf9958PkG6dE+eZfH0MPd71aU69+fJc8GcKvhABGoTSVHwIaQDjfS1NLq8VAP4KOelfsJ5s0NRyTQT7UbiD0uaEC6bwzSzLw0Ubr60sJ9f1mnS09/wzvHOkjAYYD8TaSFcdP5nPPWkCqbkwnGMPaV0DoOKaxJcbAR4MA5QIrN6g7SYCBBihP8jXO2dX7NFGl+p/MN21ooGWKWyzJn46btPp9UWP+eWYiM3IG1J9LWfjLSdNkiRfJ2mTqGhFp9D1ExvjIyzTA+hYoaIUR8CiAdOb7lqXv6MOWppJJQZM5zICIelrxJc8hQmrTUWrMXiehRbdaAJfcTf1AgMBAAECggEAEc3lVVglX23V+rwNj6eYEEoyVP7cWwpbt2zUjv7EwBjG5Yj8/qs4C+whddKMous3iK9qjyGp/PnzDxoLfCjvqll8L5/l2g5H9HRYb9BBkri1sFHfLJtvAukxkff44O7HKq3MFv+OJFFYl8Rn9wz2LWynII5hiyK1xfy9XIDY6TqM6sbRiEqKohVKEJwktrbyt7zZ5HsTX8c2desxdcvcR8tIXGRnCPHciLAICZ9UMVw68Od+572DssBw57cUhj1eAZzYUIbw6Jeopq+nfaYo5mLacjezM4pvtvQ7NuXRW20MKotW2NbJ7bBfyjWyPBaiwj9pPXtkrphsjolXbHFJcQKBgQDU0pJEllwiNeiMkaTAcCnRTheSF3VJcGi/7dPH1E3IVO1dkftLrkb5OUaE/ZAKOj/G8CHDOxKAHMJq6lZrd8A9S0eaCA7fBPlAni9hEV2Sj9hi9PkIqQofE3U8/MNO0VL9zNruJDwHNVN3Tfiv4C6CmkAXPpLVqgNj5v6IY3SCUQKBgQDWdP8K2oGECcfCbQyc2r3/zSOm4x6UCVz9hXnwENzzFK4Gem3r172FJJ4tlxBMwZeggMAJxxmvd8opTtNQahTu5Fs3CRq988t1u2awtMPgAZxbyAGTnX20TX1D2usbvI8m67qQoIg/DYfLZ1L2GxbEDUyvf89vr2U/GsEOqQtuZQKBgD+7l4XmqUytbCi8bOQcMsm1YyWi1MLbYOMpK6TKFUkK9dJxpxmw5Rw5ZL7q2DnX28WNn+7BP/cRpb8y0hJ42B8C7jxYcWukJ6iMNpARDT6YySpJRlKrnJZV02QiJbLyyLGsqVtrinZ2J7qwpOq2bfc6jjrnyD0oHr/KKytY7UkhAoGBAInG8HOQndSuU9/dH1VGUvqcWGNHVsXUniFiN0y8CLdCees/jI8QM3nuZJD1lEs7tKYxES828pGAbCjNs4TeIkS4AMIC32NZ1UT1+Ktd9tlbL5Dnldu4OqIzhl6prpJLOczPdTSlbeLUVoGfB/WEwcOCp9m4SxR9opZAIffBMs7hAoGBAKsPKsLUn/GDXTj0U6nmGeiBo2Gxj7LCY3VWlGpn5FQ1ZMRFq05Golrxb2qDbP79Z/cH/v56gE1DyFRxQFHddGasdkH1ZFkDG73FLKVsnc0ZO1HiqkB60HGZkRDFJWscvXUO+bFNCGIKh8xQXSIvoeGU7onhlDEeyWam7/UUxMqi",
                                                                 Map.of("fsp2",
                                                             "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsklJtmJYKzsH/fefD5BunRPnmXx9DD3e9WlOvfnyXPBnCr4QARqE0lR8CGkA430tTS6vFQD+CjnpX7CebNDUck0E+1G4g9LmhAum8M0sy8NFG6+tLCfX9Zp0tPf8M7xzpIwGGA/E2khXHT+Zzz1pAqm5MJxjD2ldA6DimsSXGwEeDAOUCKzeoO0mAgQYoT/I1ztnV+zRRpfqfzDdtaKBlilssyZ+Om7T6fVFj/nlmIjNyBtSfS1n4y0nTZIkXydpk6hoRafQ9RMb4yMs0wPoWKGiFEfAogHTm+5al7+jDlqaSSUGTOcyAiHpa8SXPIUJq01FqzF4noUW3WgCX3E39QIDAQAB",
                                                             "fsp3",
                                                             "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAsklJtmJYKzsH/fefD5BunRPnmXx9DD3e9WlOvfnyXPBnCr4QARqE0lR8CGkA430tTS6vFQD+CjnpX7CebNDUck0E+1G4g9LmhAum8M0sy8NFG6+tLCfX9Zp0tPf8M7xzpIwGGA/E2khXHT+Zzz1pAqm5MJxjD2ldA6DimsSXGwEeDAOUCKzeoO0mAgQYoT/I1ztnV+zRRpfqfzDdtaKBlilssyZ+Om7T6fVFj/nlmIjNyBtSfS1n4y0nTZIkXydpk6hoRafQ9RMb4yMs0wPoWKGiFEfAogHTm+5al7+jDlqaSSUGTOcyAiHpa8SXPIUJq01FqzF4noUW3WgCX3E39QIDAQAB"));
    }

    @Bean
    @Override
    public PartiesService.Settings partiesServiceSettings() {

        return new PartiesService.Settings("http://localhost:4002");
    }

    @Bean
    @Override
    public QuotesService.Settings quotesServiceSettings() {

        return new QuotesService.Settings("http://localhost:3002");
    }

    @Bean
    @Override
    public TransfersService.Settings transfersServiceSettings() {

        return new TransfersService.Settings("http://localhost:3000");
    }

}

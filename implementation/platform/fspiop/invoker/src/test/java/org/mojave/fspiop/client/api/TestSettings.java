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

package org.mojave.fspiop.client.api;

import org.mojave.fspiop.component.FspiopComponentConfiguration;
import org.mojave.fspiop.invoker.FspiopInvokerConfiguration;
import org.mojave.fspiop.invoker.api.PartiesService;
import org.mojave.fspiop.invoker.api.QuotesService;
import org.mojave.fspiop.invoker.api.TransfersService;
import org.springframework.context.annotation.Bean;

import java.util.Map;

public class TestSettings implements FspiopInvokerConfiguration.RequiredSettings {

    @Override
    public FspiopInvokerConfiguration.TransportSettings fspiopInvokerTransportSettings() {

        return new FspiopInvokerConfiguration.TransportSettings(false, null, null, true);
    }

    @Bean
    @Override
    public FspiopComponentConfiguration.ParticipantSettings participantSettings() {

        return new FspiopComponentConfiguration.ParticipantSettings(
            "fsp1", "FSP 1", "This_is_ILP_$ecret", true, true,
            "-----BEGIN PRIVATE KEY-----\nMIIEvAIBADANBgkqhkiG9w0BAQEFAASCBKYwggSiAgEAAoIBAQCv5/S8dgS1TjP5\nUIVYIDnljX1LqQSS1dFedQLLMs7GO4hXZqWi/YzC4Bs5h7ouu9rOQZm3NPOLX5L+\nejEG8le0MDEz2opcsy6r6maS7rl1ZYFTJC3i5GSm7pr5XzMrvdokzkvJyCZcPBY3\nl83M3mWtNF2LAxUH1Kt6SqGcmMgFY7wTi1nBjiP5CvW9JGPg/FUszEiS3FBWJRKs\nWfZZV8UznBmx63fesZBKBm/1NgUoCVADcNn3IYlZHuH+3Pp14u7m1fSj8vbRGwUl\nnhHAFkvioLCEzv5xO/3j9RZhZQvSKaebnnDpoVL5hukDCUk82Linaa1Oe+q5aMRR\n9n6vgGHrAgMBAAECggEAR8CBe0hWpk/2cllucuJ+S0z6EV0poO13wCRRleotHv4U\noLYdlU0AWFNKt55OCxcUavKmTW1bdHvxPT1Bd1ht/vFzeHfJ3YM3Y+eynHelDaZw\naKYG05ifF9qXO2YiPNLtwvqlPnMWdqQJ2fZhe5Ix/kMwbu48a1vL8I+1uWc2m7+D\n2BeR0C6CeoSI/PXDPuhihL5PHCmM4EUsWX8dfL8Nq+4ktJxw7fYOP4UaVCuFNx96\nzezrM1RSJG5iNKqNjAG92NOZPUiwTXsauQHbNdAP3qEAw8JmqJZ7/IWKfM0i9XGW\nWh9tm0qlPYzDpWSZXfVO3qGR1E57cXI5d6dD5BDExQKBgQDkTlTDg3BTcTLPmoCE\noLxukSyrrey4jAaWf58MGl+QZRhljmFj4zOwTPjh9OMsvv++tlUCh86ypGTydzk2\nHbBU9ZL6soHtn/SIwHM0SUqtHKcZqsrbjp58lush71e3E+xPXjQCMXCgDzbb5+vu\ny8QUcmQJjYC+mzlgApkhwI6+twKBgQDFPm59YoIhU3xHMp7j8RcWSsqFac+mjtiC\na/LkztR7glTegwOmSAauSVW5PGSMYJ9nnvrNSUzqfjtW9XV7febdAoJY5g0YoKEu\nWgFkgOGokDbpmTJt3kLtJ5Soo/yfYWftjDUUla8nhlSn0e9ADbNGhHQmtjMxD1v8\nJ2e1uOJCbQKBgCkDPUzmBr6mnicXLTEmItOF9s1ccCLy3Jgakvq8niA1WP2dZzdn\ncDiiiy8kK880/IJJBNpwhRwoVg4MhtWG3c9VUjMA8EA8tNQAJnHlHKoedJIy0UBB\nzYy6G8E72rkDtjbHyHYAZagLM6KrdgGuzymRJxFkPxV+kv8BWHrsuXffAoGATxjH\nJB7hwk2BU+fX0d63fGgHqNKSw98ascpqQBk1GkZclFKDnyXZEAE2kC7Iv4Zrxhj5\nyGLxpkNytl9+ekQ11UERVmcCr3KS0EylrFEGrRsP+kNF9ssfmYy6z5nbT2q3S4pj\nKtsPv1DeF+JXCB+2Wbv5/CAqjSz0nTyMGVKH4U0CgYAe6AQcrQfg8ok6lTmZB+3d\nqLyE64bSadRa7VsNJkY6/3LoqdFZKNKXgYfYH6wuTDE5upD3xXdBN3IUFqRgN9jY\nAuQBfdRswe5p6LfxASnjB8lQgn76CvIyeyM+1RF3C+i3sbF/Pr3uKAUPvsovfMwX\nHyFsQTxMTbJaGmnoi6X+gQ==\n-----END PRIVATE KEY-----",
            Map.of(
                "fsp2",
                "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+f0vHYEtU4z+VCFWCA5\n5Y19S6kEktXRXnUCyzLOxjuIV2alov2MwuAbOYe6LrvazkGZtzTzi1+S/noxBvJX\ntDAxM9qKXLMuq+pmku65dWWBUyQt4uRkpu6a+V8zK73aJM5LycgmXDwWN5fNzN5l\nrTRdiwMVB9SrekqhnJjIBWO8E4tZwY4j+Qr1vSRj4PxVLMxIktxQViUSrFn2WVfF\nM5wZset33rGQSgZv9TYFKAlQA3DZ9yGJWR7h/tz6deLu5tX0o/L20RsFJZ4RwBZL\n4qCwhM7+cTv94/UWYWUL0imnm55w6aFS+YbpAwlJPNi4p2mtTnvquWjEUfZ+r4Bh\n6wIDAQAB\n-----END PUBLIC KEY-----",
                "fsp3",
                "-----BEGIN PUBLIC KEY-----\nMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr+f0vHYEtU4z+VCFWCA5\n5Y19S6kEktXRXnUCyzLOxjuIV2alov2MwuAbOYe6LrvazkGZtzTzi1+S/noxBvJX\ntDAxM9qKXLMuq+pmku65dWWBUyQt4uRkpu6a+V8zK73aJM5LycgmXDwWN5fNzN5l\nrTRdiwMVB9SrekqhnJjIBWO8E4tZwY4j+Qr1vSRj4PxVLMxIktxQViUSrFn2WVfF\nM5wZset33rGQSgZv9TYFKAlQA3DZ9yGJWR7h/tz6deLu5tX0o/L20RsFJZ4RwBZL\n4qCwhM7+cTv94/UWYWUL0imnm55w6aFS+YbpAwlJPNi4p2mtTnvquWjEUfZ+r4Bh\n6wIDAQAB\n-----END PUBLIC KEY-----"));
    }

    @Bean
    @Override
    public PartiesService.Settings partiesServiceSettings() {

        return new PartiesService.Settings("http://localhost:4303");
    }

    @Bean
    @Override
    public QuotesService.Settings quotesServiceSettings() {

        return new QuotesService.Settings("http://localhost:4304");
    }

    @Bean
    @Override
    public TransfersService.Settings transfersServiceSettings() {

        return new TransfersService.Settings("http://localhost:4305");
    }

}

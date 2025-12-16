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
package org.mojave.core.transfer.domain.disruptor.financial;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mojave.core.transfer.contract.command.step.financial.ReservePayerPositionStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class ReservePayerPositionDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<ReservePayerPositionDisruptor.Event> reservePayerPositionDisruptor(
            ThreadFactory disruptorThreadFactory,
            ReservePayerPositionStep reservePayerPositionStep) {

            var disruptor = new Disruptor<>(
                ReservePayerPositionDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(reservePayerPositionStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<ReservePayerPositionDisruptor.Event> reservePayerPositionDisruptorRingBuffer(
            Disruptor<ReservePayerPositionDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<ReservePayerPositionDisruptor.Event> {

        private final ReservePayerPositionStep reservePayerPositionStep;

        public Handler(ReservePayerPositionStep reservePayerPositionStep) {

            assert reservePayerPositionStep != null;

            this.reservePayerPositionStep = reservePayerPositionStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.reservePayerPositionStep.execute(input);

            } catch (Exception e) {

            }
        }

    }

    @Getter
    @Setter
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Event {

        private String requestId;

        private ReservePayerPositionStep.Input input;

    }

}

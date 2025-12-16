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
import org.mojave.core.transfer.contract.command.step.financial.FulfilPositionsStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class FulfilPositionsDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<FulfilPositionsDisruptor.Event> fulfilPositionsDisruptor(ThreadFactory disruptorThreadFactory,
                                                                                  FulfilPositionsStep fulfilPositionsStep) {

            var disruptor = new Disruptor<>(
                FulfilPositionsDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(fulfilPositionsStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<FulfilPositionsDisruptor.Event> fulfilPositionsDisruptorRingBuffer(
            Disruptor<FulfilPositionsDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<FulfilPositionsDisruptor.Event> {

        private final FulfilPositionsStep fulfilPositionsStep;

        public Handler(FulfilPositionsStep fulfilPositionsStep) {

            assert fulfilPositionsStep != null;

            this.fulfilPositionsStep = fulfilPositionsStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.fulfilPositionsStep.execute(input);

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

        private FulfilPositionsStep.Input input;

    }

}

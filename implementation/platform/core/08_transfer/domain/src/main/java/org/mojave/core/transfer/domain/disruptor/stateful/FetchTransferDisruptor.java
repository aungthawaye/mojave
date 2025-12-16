package org.mojave.core.transfer.domain.disruptor.stateful;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mojave.core.transfer.contract.command.step.stateful.FetchTransferStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class FetchTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<FetchTransferDisruptor.Event> fetchTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            FetchTransferStep fetchTransferStep) {

            var disruptor = new Disruptor<>(
                FetchTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(fetchTransferStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<FetchTransferDisruptor.Event> fetchTransferDisruptorRingBuffer(
            Disruptor<FetchTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<FetchTransferDisruptor.Event> {

        private final FetchTransferStep fetchTransferStep;

        public Handler(FetchTransferStep fetchTransferStep) {

            assert fetchTransferStep != null;

            this.fetchTransferStep = fetchTransferStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.fetchTransferStep.execute(input);

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

        private FetchTransferStep.Input input;

    }

}

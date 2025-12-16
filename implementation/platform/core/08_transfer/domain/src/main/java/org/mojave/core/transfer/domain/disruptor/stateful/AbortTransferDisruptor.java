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
import org.mojave.core.transfer.contract.command.step.stateful.AbortTransferStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class AbortTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<AbortTransferDisruptor.Event> abortTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            AbortTransferStep abortTransferStep) {

            var disruptor = new Disruptor<>(
                AbortTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(abortTransferStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<AbortTransferDisruptor.Event> abortTransferDisruptorRingBuffer(
            Disruptor<AbortTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<AbortTransferDisruptor.Event> {

        private final AbortTransferStep abortTransferStep;

        public Handler(AbortTransferStep abortTransferStep) {

            assert abortTransferStep != null;

            this.abortTransferStep = abortTransferStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.abortTransferStep.execute(input);

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

        private AbortTransferStep.Input input;

    }

}

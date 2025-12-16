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
import org.mojave.core.transfer.domain.command.step.stateful.DisputeTransferStepHandler;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class DisputeTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<DisputeTransferDisruptor.Event> disputeTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            DisputeTransferStepHandler disputeTransferStepHandler) {

            var disruptor = new Disruptor<>(
                DisputeTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(disputeTransferStepHandler));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<DisputeTransferDisruptor.Event> disputeTransferDisruptorRingBuffer(
            Disruptor<DisputeTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<DisputeTransferDisruptor.Event> {

        private final DisputeTransferStepHandler disputeTransferStepHandler;

        public Handler(DisputeTransferStepHandler disputeTransferStepHandler) {

            assert disputeTransferStepHandler != null;

            this.disputeTransferStepHandler = disputeTransferStepHandler;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.disputeTransferStepHandler.execute(input);

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

        private DisputeTransferStepHandler.Input input;

    }

}

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
import org.mojave.core.transfer.contract.command.step.stateful.ReceiveTransferStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class ReceiveTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<ReceiveTransferDisruptor.Event> receiveTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            ReceiveTransferStep receiveTransferStep) {

            var disruptor = new Disruptor<>(
                ReceiveTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(receiveTransferStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<ReceiveTransferDisruptor.Event> receiveTransferDisruptorRingBuffer(
            Disruptor<ReceiveTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<ReceiveTransferDisruptor.Event> {

        private final ReceiveTransferStep receiveTransferStep;

        public Handler(ReceiveTransferStep receiveTransferStep) {

            assert receiveTransferStep != null;

            this.receiveTransferStep = receiveTransferStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.receiveTransferStep.execute(input);

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

        private ReceiveTransferStep.Input input;

    }

}

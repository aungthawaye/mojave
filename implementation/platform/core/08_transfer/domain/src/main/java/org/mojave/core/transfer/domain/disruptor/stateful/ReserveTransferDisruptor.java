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
import org.mojave.core.transfer.contract.command.step.stateful.ReserveTransferStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class ReserveTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<ReserveTransferDisruptor.Event> reserveTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            ReserveTransferStep reserveTransferStep) {

            var disruptor = new Disruptor<>(
                ReserveTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(reserveTransferStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<ReserveTransferDisruptor.Event> reserveTransferDisruptorRingBuffer(
            Disruptor<ReserveTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<ReserveTransferDisruptor.Event> {

        private final ReserveTransferStep reserveTransferStep;

        public Handler(ReserveTransferStep reserveTransferStep) {

            assert reserveTransferStep != null;

            this.reserveTransferStep = reserveTransferStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.reserveTransferStep.execute(input);

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

        private ReserveTransferStep.Input input;

    }

}

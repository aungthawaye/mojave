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

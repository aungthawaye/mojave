package org.mojave.core.transfer.domain.disruptor.fspiop;

import com.lmax.disruptor.BlockingWaitStrategy;
import com.lmax.disruptor.EventHandler;
import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;
import com.lmax.disruptor.dsl.ProducerType;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.mojave.core.transfer.contract.command.step.fspiop.UnwrapRequestStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class UnwrapRequestDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<UnwrapRequestDisruptor.Event> unwrapRequestDisruptor(
            ThreadFactory disruptorThreadFactory,
            UnwrapRequestStep unwrapRequestStep) {

            var disruptor = new Disruptor<>(
                UnwrapRequestDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(unwrapRequestStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<UnwrapRequestDisruptor.Event> unwrapRequestDisruptorRingBuffer(
            Disruptor<UnwrapRequestDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<UnwrapRequestDisruptor.Event> {

        private final UnwrapRequestStep unwrapRequestStep;

        public Handler(UnwrapRequestStep unwrapRequestStep) {

            assert unwrapRequestStep != null;

            this.unwrapRequestStep = unwrapRequestStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.unwrapRequestStep.execute(input);

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

        private UnwrapRequestStep.Input input;

    }

}

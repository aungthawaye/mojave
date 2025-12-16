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
import org.mojave.core.transfer.contract.command.step.fspiop.ForwardToDestinationStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class ForwardToDestinationDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<ForwardToDestinationDisruptor.Event> forwardToDestinationDisruptor(
            ThreadFactory disruptorThreadFactory,
            ForwardToDestinationStep forwardToDestinationStep) {

            var disruptor = new Disruptor<>(
                ForwardToDestinationDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(forwardToDestinationStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<ForwardToDestinationDisruptor.Event> forwardToDestinationDisruptorRingBuffer(
            Disruptor<ForwardToDestinationDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<ForwardToDestinationDisruptor.Event> {

        private final ForwardToDestinationStep forwardToDestinationStep;

        public Handler(ForwardToDestinationStep forwardToDestinationStep) {

            assert forwardToDestinationStep != null;

            this.forwardToDestinationStep = forwardToDestinationStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.forwardToDestinationStep.execute(input);

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

        private ForwardToDestinationStep.Input input;

    }

}

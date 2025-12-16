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
import org.mojave.core.transfer.domain.command.step.stateful.CommitTransferStepHandler;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class CommitTransferDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<CommitTransferDisruptor.Event> commitTransferDisruptor(
            ThreadFactory disruptorThreadFactory,
            CommitTransferStepHandler commitTransferStepHandler) {

            var disruptor = new Disruptor<>(
                CommitTransferDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(commitTransferStepHandler));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<CommitTransferDisruptor.Event> commitTransferDisruptorRingBuffer(
            Disruptor<CommitTransferDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<CommitTransferDisruptor.Event> {

        private final CommitTransferStepHandler commitTransferStepHandler;

        public Handler(CommitTransferStepHandler commitTransferStepHandler) {

            assert commitTransferStepHandler != null;

            this.commitTransferStepHandler = commitTransferStepHandler;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.commitTransferStepHandler.execute(input);

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

        private CommitTransferStepHandler.Input input;

    }

}

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
import org.mojave.core.transfer.domain.command.step.fspiop.CommitTransferToPayerStepHandler;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class CommitTransferToPayerDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<CommitTransferToPayerDisruptor.Event> commitTransferToPayerDisruptor(
            ThreadFactory disruptorThreadFactory,
            CommitTransferToPayerStepHandler commitTransferToPayerStepHandler) {

            var disruptor = new Disruptor<>(
                CommitTransferToPayerDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(commitTransferToPayerStepHandler));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<CommitTransferToPayerDisruptor.Event> commitTransferToPayerDisruptorRingBuffer(
            Disruptor<CommitTransferToPayerDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<CommitTransferToPayerDisruptor.Event> {

        private final CommitTransferToPayerStepHandler commitTransferToPayerStepHandler;

        public Handler(CommitTransferToPayerStepHandler commitTransferToPayerStepHandler) {

            assert commitTransferToPayerStepHandler != null;

            this.commitTransferToPayerStepHandler = commitTransferToPayerStepHandler;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.commitTransferToPayerStepHandler.execute(input);

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

        private CommitTransferToPayerStepHandler.Input input;

    }

}

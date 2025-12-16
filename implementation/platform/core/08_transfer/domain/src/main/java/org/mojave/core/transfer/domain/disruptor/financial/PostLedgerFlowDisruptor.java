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
import org.mojave.core.transfer.contract.command.step.financial.PostLedgerFlowStep;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class PostLedgerFlowDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<PostLedgerFlowDisruptor.Event> postLedgerFlowDisruptor(
            ThreadFactory disruptorThreadFactory,
            PostLedgerFlowStep postLedgerFlowStep) {

            var disruptor = new Disruptor<>(
                PostLedgerFlowDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(postLedgerFlowStep));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<PostLedgerFlowDisruptor.Event> postLedgerFlowDisruptorRingBuffer(
            Disruptor<PostLedgerFlowDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<PostLedgerFlowDisruptor.Event> {

        private final PostLedgerFlowStep postLedgerFlowStep;

        public Handler(PostLedgerFlowStep postLedgerFlowStep) {

            assert postLedgerFlowStep != null;

            this.postLedgerFlowStep = postLedgerFlowStep;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.postLedgerFlowStep.execute(input);

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

        private PostLedgerFlowStep.Input input;

    }

}

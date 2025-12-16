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
import org.mojave.core.transfer.domain.command.step.fspiop.PatchTransferToPayeeStepHandler;
import org.springframework.context.annotation.Bean;

import java.util.concurrent.ThreadFactory;

public class PatchTransferToPayeeDisruptor {

    public static class Configuration {

        @Bean
        public Disruptor<PatchTransferToPayeeDisruptor.Event> patchTransferToPayeeDisruptor(
            ThreadFactory disruptorThreadFactory,
            PatchTransferToPayeeStepHandler patchTransferToPayeeStepHandler) {

            var disruptor = new Disruptor<>(
                PatchTransferToPayeeDisruptor.Event::new, 1024, disruptorThreadFactory,
                ProducerType.MULTI, new BlockingWaitStrategy());

            disruptor.handleEventsWith(new Handler(patchTransferToPayeeStepHandler));
            disruptor.start();

            return disruptor;
        }

        @Bean
        public RingBuffer<PatchTransferToPayeeDisruptor.Event> patchTransferToPayeeDisruptorRingBuffer(
            Disruptor<PatchTransferToPayeeDisruptor.Event> disruptor) {

            return disruptor.getRingBuffer();
        }

    }

    public static class Handler implements EventHandler<PatchTransferToPayeeDisruptor.Event> {

        private final PatchTransferToPayeeStepHandler patchTransferToPayeeStepHandler;

        public Handler(PatchTransferToPayeeStepHandler patchTransferToPayeeStepHandler) {

            assert patchTransferToPayeeStepHandler != null;

            this.patchTransferToPayeeStepHandler = patchTransferToPayeeStepHandler;
        }

        @Override
        public void onEvent(Event event, long sequence, boolean endOfBatch) {

            var input = event.getInput();

            try {

                this.patchTransferToPayeeStepHandler.execute(input);

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

        private PatchTransferToPayeeStepHandler.Input input;

    }

}

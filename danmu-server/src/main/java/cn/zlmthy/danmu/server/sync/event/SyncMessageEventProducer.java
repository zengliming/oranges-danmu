package cn.zlmthy.danmu.server.sync.event;

import com.lmax.disruptor.EventTranslatorOneArg;
import com.lmax.disruptor.EventTranslatorVararg;
import com.lmax.disruptor.RingBuffer;

/**
 * @author zengliming
 * @date 2019/9/12
 */
public class SyncMessageEventProducer {

    private static final EventTranslatorOneArg<SyncMessageEvent, String> TRANSLATOR = (syncMessageEvent,seq, message) -> {
        syncMessageEvent.setMessage(message);
    };

    private final RingBuffer<SyncMessageEvent> ringBuffer;

    public SyncMessageEventProducer(RingBuffer<SyncMessageEvent> ringBuffer) {
        this.ringBuffer = ringBuffer;
    }

    public void syncMessage(String message) {
        this.ringBuffer.publishEvent(TRANSLATOR, message);
    }
}

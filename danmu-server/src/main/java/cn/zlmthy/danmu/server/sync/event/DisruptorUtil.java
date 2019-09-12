package cn.zlmthy.danmu.server.sync.event;

import com.lmax.disruptor.RingBuffer;
import com.lmax.disruptor.dsl.Disruptor;

import java.util.concurrent.ThreadFactory;

/**
 * @author zengliming
 * @date 2019/9/12
 */
public class DisruptorUtil {

    static Disruptor<SyncMessageEvent> disruptor = null;

    static {
        SyncMessageEventFactory factory = new SyncMessageEventFactory();
        int ringBufferSize = 1024;
        ThreadFactory threadFactory = new ThreadFactory() {
            @Override
            public Thread newThread(Runnable runnable) {
                return new Thread(runnable);
            }
        };

        disruptor = new Disruptor<SyncMessageEvent>(factory, ringBufferSize, threadFactory);
        disruptor.handleEventsWith(new SyncMessageConsumer());
        disruptor.start();
    }

    public static void producer(String message) {
        RingBuffer<SyncMessageEvent> ringBuffer = disruptor.getRingBuffer();
        SyncMessageEventProducer producer = new SyncMessageEventProducer(ringBuffer);
        producer.syncMessage(message);
    }
}

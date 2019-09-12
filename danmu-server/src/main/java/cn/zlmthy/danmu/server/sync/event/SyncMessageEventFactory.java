package cn.zlmthy.danmu.server.sync.event;

import com.lmax.disruptor.EventFactory;

/**
 * @author zengliming
 * @date 2019/9/12
 */
public class SyncMessageEventFactory implements EventFactory<SyncMessageEvent> {

    @Override
    public SyncMessageEvent newInstance() {
        return new SyncMessageEvent();
    }
}

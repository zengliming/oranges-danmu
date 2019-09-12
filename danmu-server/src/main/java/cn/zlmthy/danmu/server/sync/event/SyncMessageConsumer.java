package cn.zlmthy.danmu.server.sync.event;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.server.sync.SyncClient;
import com.lmax.disruptor.EventHandler;

/**
 * @author zengliming
 * @date 2019/9/12
 */
public class SyncMessageConsumer implements EventHandler<SyncMessageEvent> {

    @Override
    public void onEvent(SyncMessageEvent syncMessageEvent, long l, boolean b) throws Exception {
        SyncClient.sendSyncMessage(syncMessageEvent.getMessage());
    }
}

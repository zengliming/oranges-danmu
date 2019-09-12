package cn.zlmthy.danmu.server.sync.event;

import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @author zengliming
 * @date 2019/9/12
 */
@Data
@NoArgsConstructor
public class SyncMessageEvent {

    private String message;
}

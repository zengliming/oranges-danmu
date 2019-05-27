package cn.zlmthy.danmu.server.sync.handle;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.commons.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.log4j.Log4j2;

/**
 * @author zengliming
 * @date 2019/5/27
 * @since 1.0.0
 */
@Log4j2
public class KryoEncoder extends MessageToByteEncoder<SyncMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SyncMessage message, ByteBuf out) throws Exception {
        byte[] bytes = KryoSerializer.serialize(message);
        log.info("message length [{}]", bytes.length);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
        ctx.flush();
    }

}

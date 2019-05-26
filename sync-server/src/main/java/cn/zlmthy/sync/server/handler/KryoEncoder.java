package cn.zlmthy.sync.server.handler;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.commons.kryo.KryoSerializer;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;

/**
 * @author zengliming
 * @date 2019/5/27
 * @since 1.0.0
 */
public class KryoEncoder extends MessageToByteEncoder<SyncMessage> {

    @Override
    protected void encode(ChannelHandlerContext ctx, SyncMessage message, ByteBuf out) throws Exception {
        KryoSerializer.serialize(message, out);
        ctx.flush();
    }

}

package cn.zlmthy.danmu.server.sync.handle;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.server.config.NettyConfig;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.time.LocalDateTime;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class SyncClientHandler extends SimpleChannelInboundHandler<SyncMessage> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SyncMessage msg) throws Exception {

        log.info("客户端接收到的消息： {}",msg);

        NettyConfig.groupSend(msg.getMessage());

        //完成通信后关闭连接
        //ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("与同步服务断开连接");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常{}", cause.getMessage());
        ctx.close();
    }
}

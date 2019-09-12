package cn.zlmthy.danmu.server.sync.handle;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.server.config.NettyConfig;
import cn.zlmthy.danmu.server.sync.SyncClient;
import cn.zlmthy.danmu.server.sync.core.IConnection;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.EventLoop;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class SyncClientHandler extends SimpleChannelInboundHandler<SyncMessage> {

    private IConnection imConnection = new IConnection();

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SyncMessage msg) throws Exception {

        log.info("客户端接收到的消息： {}",msg);

        NettyConfig.groupSend(msg.getMessage());

        //完成通信后关闭连接
        //ctx.close();
    }

    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {

        log.info("与同步服务连接成功");
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.error("与同步服务器连接断线");
        //使用过程中断线重连
        final EventLoop eventLoop = ctx.channel().eventLoop();
        eventLoop.schedule(new Runnable() {
            @Override
            public void run() {
                imConnection.connect(SyncClient.host, SyncClient.serverPort);
            }
        }, 1L, TimeUnit.SECONDS);
        super.channelInactive(ctx);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("发生异常{}", cause.getMessage());
        ctx.close();
    }
}

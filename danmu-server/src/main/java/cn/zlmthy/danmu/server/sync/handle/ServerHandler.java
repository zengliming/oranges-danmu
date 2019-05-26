package cn.zlmthy.danmu.server.sync.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.util.UUID;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
public class ServerHandler extends SimpleChannelInboundHandler<String> {

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws   Exception {
        //接收到的数据
        System.out.println(ctx.channel().remoteAddress()+" , "+msg);

        //返回给客户端的数据
        ctx.channel().writeAndFlush("server: "+ UUID.randomUUID());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws  Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

package cn.zlmthy.danmu.server.sync.handle;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;

import java.time.LocalDateTime;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
public class ClientHandler extends SimpleChannelInboundHandler<String> {

    //接收服务端数据&发送数据
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, String msg) throws Exception {

        System.out.println("客户端接收到的消息： "+msg);

        ctx.writeAndFlush(LocalDateTime.now());

        //完成通信后关闭连接
        //ctx.close();
    }

    //和服务器建立连接
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush("在吗！！！！");
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }
}

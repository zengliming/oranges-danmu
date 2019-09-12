package cn.zlmthy.sync.server.handler;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import io.netty.channel.Channel;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class SyncServerHandler extends SimpleChannelInboundHandler<SyncMessage> {

    private static ChannelGroup channelGroup = new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);


    @Override
    protected void channelRead0(ChannelHandlerContext ctx, SyncMessage msg) throws Exception {
        Channel channel = ctx.channel();
        log.info("收到弹幕服务推送[{}]", msg);
        channelGroup.forEach(ch->{
            if(ch != channel){
                ch.writeAndFlush(msg);
            }
        });
    }
    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        log.info("发生异常{}", cause.getMessage());
    }
    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel channel = ctx.channel();
        log.info("弹幕服务[" + channel.remoteAddress() + "]加入，当前服务"+(channelGroup.size()+1)+"");
        channelGroup.add(channel);
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        log.info("弹幕服务[{}]上线" , ctx.channel().remoteAddress());
    }
    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        log.info("弹幕服务[{}]下线" , ctx.channel().remoteAddress());
        channelGroup.remove(ctx.channel());
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        channelGroup.remove(ctx.channel());
    }
}

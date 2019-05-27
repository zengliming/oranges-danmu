package cn.zlmthy.danmu.server.handle;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.server.config.NettyConfig;
import cn.zlmthy.danmu.server.sync.SyncClient;
import io.netty.channel.*;
import io.netty.handler.codec.http.websocketx.*;
import io.netty.handler.timeout.IdleStateEvent;
import io.netty.util.AttributeKey;
import lombok.extern.log4j.Log4j2;

import java.util.Date;

/**
 * @author zengliming
 * @date 2019/5/25
 * @since 1.0.0
 */
@Log4j2
public class WebSocketServerHandler extends SimpleChannelInboundHandler<WebSocketFrame> {


    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
    }
    @Override
    protected void channelRead0(ChannelHandlerContext ctx, WebSocketFrame msg) throws Exception {
        //判断是否是关闭websocket的指令
        if (msg instanceof CloseWebSocketFrame) {
            NettyConfig.removeChannel(ctx.channel());
        }
        //判断是否是ping消息
        if (msg instanceof PingWebSocketFrame) {
            log.info("ping");
            ctx.channel().write(new PongWebSocketFrame(msg.content().retain()));
            return;
        }

        //判断是否是二进制消息，如果是二进制消息，抛出异常
        if( ! (msg instanceof TextWebSocketFrame) ){
            log.info("目前我们不支持二进制消息");
            throw new RuntimeException("【"+this.getClass().getName()+"】不支持消息");
        }
        //返回应答消息
        //获取客户端向服务端发送的消息
        String request = ((TextWebSocketFrame) msg).text();
        log.info("服务端收到客户端的消息====>>>" + request);
        SyncMessage syncMessage = new SyncMessage();
        syncMessage.setMessage(request);
        SyncClient.sendSyncMessage(syncMessage);
        TextWebSocketFrame tws = new TextWebSocketFrame(new Date().toString()                                                                               + ctx.channel().id()    + " ===>>> " + request);
        //服务端向每个连接上来的客户端群发消息
        NettyConfig.channelGroup.writeAndFlush(tws);
    }


    @Override
    public void handlerAdded(ChannelHandlerContext ctx) throws Exception {
        Channel incoming = ctx.channel();
        log.info("{}加入", incoming.remoteAddress());
        for (Channel channel : NettyConfig.channelGroup) {
            channel.writeAndFlush(new TextWebSocketFrame("[SERVER] - " + incoming.remoteAddress() + " 加入"));
        }
        NettyConfig.addChannel(ctx.channel());
    }

    @Override
    public void userEventTriggered(ChannelHandlerContext ctx, Object evt) throws Exception {

        if (evt instanceof IdleStateEvent){
            IdleStateEvent event = (IdleStateEvent)evt;

            String idleType = null;
            switch(event.state()){
                case READER_IDLE:
                    idleType = "读空闲";
                    break;
                case WRITER_IDLE:
                    idleType = "写空闲";
                    break;
                default:
                    idleType = "读写空闲";
            }
            log.info(ctx.channel().remoteAddress() + " " + idleType);
            ctx.channel().close();
        }

        if (evt instanceof WebSocketServerProtocolHandler.ServerHandshakeStateEvent) {

            // 移除性能更加
            ctx.pipeline().remove(HttpRequestHandler.class);

            boolean hasAuth = false;
            String userName = null;
            String token = ctx.channel().attr(HttpRequestHandler.TOKEN).get();
            log.info("token is [{}]", token);
            if (token == null || "".equals(token)) {
                log.warn("no token");
            } else {
                hasAuth = true;
            }
            if (!hasAuth) {
                String noToken = "［您的token非法，请重新登录］";
                log.warn(noToken);
                ctx.writeAndFlush(noToken).addListener(new ChannelFutureListener() {
                    @Override
                    public void operationComplete(ChannelFuture channelFuture) throws Exception {
                        channelFuture.channel().close();
                    }
                });
            }
        }
    }
    @Override
    public void handlerRemoved(ChannelHandlerContext ctx) throws Exception {
        log.info("用户下线: " + ctx.channel().id().asLongText());
        NettyConfig.removeChannel(ctx.channel());
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error(cause);
        NettyConfig.removeChannel(ctx.channel());
        ctx.channel().close();
    }

}

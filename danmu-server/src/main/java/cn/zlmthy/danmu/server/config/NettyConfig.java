package cn.zlmthy.danmu.server.config;

import io.netty.channel.Channel;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.handler.codec.http.websocketx.TextWebSocketFrame;
import io.netty.util.concurrent.GlobalEventExecutor;
import lombok.extern.log4j.Log4j2;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class NettyConfig {

    public static final String SOCKET_URI = "/websocket";

    public static ChannelGroup channelGroup=new DefaultChannelGroup(GlobalEventExecutor.INSTANCE);

    public static void addChannel(Channel channel){
        channelGroup.add(channel);
    }

    public static void removeChannel(Channel channel){
        channelGroup.remove(channel);
    }

    /**
     * 群发消息
     * @param message 消息内容
     */
    public static void groupSend(String message){
        channelGroup.writeAndFlush(new TextWebSocketFrame(message));
    }
}

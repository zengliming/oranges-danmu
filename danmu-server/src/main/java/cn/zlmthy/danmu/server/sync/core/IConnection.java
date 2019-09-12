package cn.zlmthy.danmu.server.sync.core;

import cn.zlmthy.danmu.server.sync.core.listener.ConnectionListener;
import cn.zlmthy.danmu.server.sync.handle.SyncClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;


/**
 * @author zengliming
 * @date 2019/9/12
 */
public class IConnection {

    private Channel channel;

    public Channel connect(String host, int port) {
        doConnect(host, port);
        return this.channel;
    }

    private void doConnect(String host, int port) {
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        Bootstrap bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new SyncClientInitializer());
        ChannelFuture channelFuture = bootstrap.connect(host, port);
        channelFuture.addListener(new ConnectionListener(host, port));
        channel = channelFuture.channel();
    }
}

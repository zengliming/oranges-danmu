package cn.zlmthy.danmu.server.sync.core.listener;

import cn.zlmthy.danmu.server.sync.core.IConnection;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoop;
import lombok.extern.log4j.Log4j2;

import java.util.concurrent.TimeUnit;

/**
 * @author zengliming
 * @date 2019/9/12
 */
@Log4j2
public class ConnectionListener implements ChannelFutureListener {

    private IConnection iConnection = new IConnection();

    private String host;

    private int port;

    public ConnectionListener(String host, int port) {
        this.host = host;
        this.port = port;
    }


    @Override
    public void operationComplete(ChannelFuture channelFuture) throws Exception {
        if (!channelFuture.isSuccess()) {
            final EventLoop loop = channelFuture.channel().eventLoop();
            loop.schedule(() -> {
                log.error("服务端链接不上，开始重连操作...");
                iConnection.connect(host, port);
            }, 1L, TimeUnit.SECONDS);
        } else {
            log.info("服务端链接成功...");
        }
    }
}

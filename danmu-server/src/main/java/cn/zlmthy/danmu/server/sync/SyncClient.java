package cn.zlmthy.danmu.server.sync;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
import cn.zlmthy.danmu.server.config.IThreadPool;
import cn.zlmthy.danmu.server.sync.core.IConnection;
import cn.zlmthy.danmu.server.sync.event.SyncMessageEvent;
import cn.zlmthy.danmu.server.sync.event.SyncMessageEventFactory;
import cn.zlmthy.danmu.server.sync.event.SyncMessageEventProducer;
import cn.zlmthy.danmu.server.sync.handle.SyncClientHandler;
import cn.zlmthy.danmu.server.sync.handle.SyncClientInitializer;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import lombok.extern.log4j.Log4j2;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class SyncClient {

    private static Channel channel;

    public static String host;

    public static int serverPort;

    public static void run(final int port) {

        serverPort = port;
        host = "127.0.0.1";
        IConnection iConnection = new IConnection();
        channel = iConnection.connect(host, serverPort);
        log.info("链接到同步服务器");
        try {
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void sendSyncMessage(String message) {
        SyncMessage syncMessage = new SyncMessage();
        syncMessage.setMessage(message);
        log.info("向同步服务器推送消息");
        channel.writeAndFlush(syncMessage);
    }


}

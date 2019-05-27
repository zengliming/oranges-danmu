package cn.zlmthy.danmu.server.sync;

import cn.zlmthy.danmu.commons.dto.SyncMessage;
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

    private static Bootstrap bootstrap;

    private static ChannelFuture channelFuture;

    private static Channel channel;

    private static int serverPort;

    private static int reTriedTime = 0;

    private static int reTryTime = 16;

    private static int reTryStep = 1;

    private static long lastRetryTime = 0;

    public static void run(final int port) {

        serverPort = port;
        EventLoopGroup eventLoopGroup = new NioEventLoopGroup();

        bootstrap = new Bootstrap();
        bootstrap.group(eventLoopGroup)
                .channel(NioSocketChannel.class)
                .handler(new SyncClientInitializer());

        try {
            channelFuture = bootstrap.connect("127.0.0.1",serverPort).sync();
            log.info("链接到同步服务器");
            channel = channelFuture.channel();
//            reTriedTime = 0;
//            lastRetryTime = System.currentTimeMillis() / 1000;
//            reTryStep = 10;
//            reTriedTime++;
//            reTryStep = 2 * reTryStep;
//            IThreadPool.execute(reConnect);
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        }finally {
            eventLoopGroup.shutdownGracefully();
        }
    }

    public static void sendSyncMessage(SyncMessage message){
        log.info("向同步服务器推送消息");
        channel.writeAndFlush(message);
    }

    private static Runnable reConnect = ()->{
        log.info("开启断线重连线程");
        while (true) {
            if (reTriedTime >= reTryTime){
                log.info("超过重试次数");
                Thread.currentThread().interrupt();
            }
            if (!channel.isActive()){
                if (System.currentTimeMillis()/1000 - lastRetryTime >= reTryStep){
                    log.info("短线重连中");
                    try {
                        channelFuture = bootstrap.connect("127.0.0.1",serverPort).sync();
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                        Thread.currentThread().interrupt();
                    }
                    log.info("链接到同步服务器");
                    channel = channelFuture.channel();
                }
            }
        }
    };

}

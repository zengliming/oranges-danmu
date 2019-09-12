package cn.zlmthy.sync.server;

import cn.zlmthy.sync.server.handler.SyncServerChannelInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.Channel;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.logging.LogLevel;
import io.netty.handler.logging.LoggingHandler;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class SyncServerMain {


    public static void main(String[] args) {
        Map<String, String> cmdArgs = new HashMap<>();
        cmdArgs.put("port", "9090");
        if (args.length > 0) {
            for (String arg : args) {
                String[] strings = arg.split("=");
                if (strings.length == 2) {
                    cmdArgs.put(strings[0], strings[1]);
                }
            }
        }

        final int port = Integer.parseInt(cmdArgs.get("port"));
        init(port);
    }

    private static void init(final int port) {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .option(ChannelOption.SO_BACKLOG, 1024)
                .handler(new LoggingHandler(LogLevel.DEBUG))
                .childHandler(new SyncServerChannelInitializer());

        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("damu sync server start in port {}", port);
            Channel channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

package cn.zlmthy.danmu.server;

import cn.zlmthy.danmu.server.config.NettyConfig;
import cn.zlmthy.danmu.server.handle.HttpRequestHandler;
import cn.zlmthy.danmu.server.handle.WebSocketServerHandler;
import cn.zlmthy.danmu.server.sync.SyncClient;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.WebSocketServerProtocolHandler;
import io.netty.handler.stream.ChunkedWriteHandler;
import io.netty.handler.timeout.IdleStateHandler;
import lombok.extern.log4j.Log4j2;
import org.apache.logging.log4j.core.config.ConfigurationSource;
import org.apache.logging.log4j.core.config.Configurator;
import org.apache.logging.log4j.core.lookup.MainMapLookup;
import org.slf4j.MDC;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

/**
 * @author zengliming
 * @date 2019/5/25
 * @since 1.0.0
 */
@Log4j2
public class DanMuServerMain {


    public static void main(String[] args) {
        Map<String, String> cmdArgs = new HashMap<>();
        cmdArgs.put("port", "8081");
        cmdArgs.put("logPath", "log4j2.xml");
        cmdArgs.put("syncPort", "9090");
        cmdArgs.put("logOutDir", "../logs");
        if (args.length > 0) {
            for (String arg : args) {
                String[] strings = arg.split("=");
                if (strings.length == 2) {
                    cmdArgs.put(strings[0], strings[1]);
                }
            }
        }
        MDC.put("logOutDir",cmdArgs.get("logOutDir"));
        String logPath = cmdArgs.get("logPath");
        System.setProperty("logOutDir",cmdArgs.get("logOutDir"));
        ConfigurationSource source;
        try {
            source = new ConfigurationSource(new FileInputStream(logPath));
            Configurator.initialize(null, source);
            log.info("加载配置文件[{}]", logPath);
            log.info("日志输出目录[{}]", cmdArgs.get("logOutDir"));
        } catch (IOException e) {
            e.printStackTrace();
        }


        final int port = Integer.parseInt(cmdArgs.get("port"));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel.class)
                .childHandler(new ChannelInitializer<SocketChannel>() {

                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        //HttpServerCodec: 针对http协议进行编解码
                        pipeline.addLast(new HttpServerCodec());
                        //ChunkedWriteHandler分块写处理，文件过大会将内存撑爆
                        pipeline.addLast(new ChunkedWriteHandler());
                        /**
                         * 作用是将一个Http的消息组装成一个完成的HttpRequest或者HttpResponse，那么具体的是什么
                         * 取决于是请求还是响应, 该Handler必须放在HttpServerCodec后的后面
                         */
                        pipeline.addLast(new HttpObjectAggregator(8192));
                        pipeline.addLast(new HttpRequestHandler());
                        //用于处理websocket, /ws为访问websocket时的uri
                        pipeline.addLast(new WebSocketServerProtocolHandler(NettyConfig.SOCKET_URI));
                        pipeline.addLast(new IdleStateHandler(120, 120, 120, TimeUnit.SECONDS));

                        pipeline.addLast(new WebSocketServerHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("damu server start in port {}", port);
            try {
                SyncClient.run(Integer.parseInt(cmdArgs.get("syncPort")));
            } catch (Exception e) {
                log.error(e);
            }
            channelFuture.channel().closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }

    }
}

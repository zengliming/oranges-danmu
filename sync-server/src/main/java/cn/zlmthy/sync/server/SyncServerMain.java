package cn.zlmthy.sync.server;

import cn.zlmthy.sync.server.handler.SyncServerHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import io.netty.handler.codec.Delimiters;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;
import io.netty.util.CharsetUtil;
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

    private static Channel channel;

    public static Channel getChannel(){
        return channel;
    }

    public static void main(String[] args) {
        Map<String, String> cmdArgs = new HashMap<>();
        cmdArgs.put("port", "9090");
        if (args.length >0){
            for (String arg : args){
                String[] strings = arg.split("=");
                if (strings.length == 2){
                    cmdArgs.put(strings[0], strings[1]);
                }
            }
        }

        final int port = Integer.parseInt(cmdArgs.get("port"));
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();

        ServerBootstrap bootstrap = new ServerBootstrap();
        bootstrap.group(bossGroup, workerGroup)
                .channel(NioServerSocketChannel .class)
                .childHandler(new ChannelInitializer<SocketChannel>() {
                    @Override
                    protected void initChannel(SocketChannel socketChannel) throws Exception {
                        ChannelPipeline pipeline = socketChannel.pipeline();
                        pipeline.addLast(new DelimiterBasedFrameDecoder(4096, Delimiters.lineDelimiter()));
                        pipeline.addLast(new StringEncoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new StringDecoder(CharsetUtil.UTF_8));
                        pipeline.addLast(new SyncServerHandler());
                    }
                });

        try {
            ChannelFuture channelFuture = bootstrap.bind(port).sync();
            log.info("damu sync server start in port {}", port);
            channel = channelFuture.channel();
            channel.closeFuture().sync();
        } catch (InterruptedException e) {
            log.error(e);
            Thread.currentThread().interrupt();
        }finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }
}

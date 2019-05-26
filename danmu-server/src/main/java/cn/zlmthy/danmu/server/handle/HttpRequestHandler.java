package cn.zlmthy.danmu.server.handle;

import cn.zlmthy.danmu.server.config.NettyConfig;
import io.netty.channel.*;
import io.netty.handler.codec.http.*;
import io.netty.util.AttributeKey;
import lombok.extern.log4j.Log4j2;

import java.util.HashMap;
import java.util.Map;

/**
 * @author zengliming
 * @date 2019/5/26
 * @since 1.0.0
 */
@Log4j2
public class HttpRequestHandler extends SimpleChannelInboundHandler<FullHttpRequest> {

    public static AttributeKey<String> TOKEN = AttributeKey.valueOf("token");

    @Override
    public void channelRead0(ChannelHandlerContext ctx, FullHttpRequest request) throws Exception {
        log.info("http [{}]", request.uri().contains(NettyConfig.SOCKET_URI));
        String uri = request.uri();
        if (request.uri().contains(NettyConfig.SOCKET_URI)) {
            request.setUri(NettyConfig.SOCKET_URI);
            String token = null;
            Map<String, String> params = parseRequestParams(uri.substring(uri.indexOf("?") + 1));
            if (params != null){
                token = params.get("token");
            }
            ctx.channel().attr(TOKEN).set(token);
            ctx.fireChannelRead(request.retain());
        } else {
            if (HttpUtil.is100ContinueExpected(request)) {
                send100Continue(ctx);
            }
            HttpResponse response = new DefaultHttpResponse(request.getProtocolVersion(), HttpResponseStatus.OK);
            boolean keepAlive = HttpUtil.isKeepAlive(request);
            if (keepAlive) {
                response.headers().set(HttpHeaderNames.CONNECTION, HttpHeaderValues.KEEP_ALIVE);
            }
            ctx.write(response);
            ChannelFuture future = ctx.writeAndFlush(LastHttpContent.EMPTY_LAST_CONTENT);
            if (!keepAlive) {
                future.addListener(ChannelFutureListener.CLOSE);
            }
        }
    }

    private Map<String, String> parseRequestParams(String params){
        Map<String, String> result = new HashMap<>();
        String[] strings = params.split("&");
        if (strings.length >= 1) {
            for (int i=0;i< strings.length;i++){
                String[] s = strings[i].split("=");
                result.put(s[0], s[1]);

            }
        }
        return result;
    }

    private static void send100Continue(ChannelHandlerContext ctx) {
        FullHttpResponse response = new DefaultFullHttpResponse(HttpVersion.HTTP_1_1, HttpResponseStatus.CONTINUE);
        ctx.writeAndFlush(response);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause)
            throws Exception {
        Channel incoming = ctx.channel();
        System.out.println("Client:" + incoming.remoteAddress() + "异常");
        // 当出现异常就关闭连接
        cause.printStackTrace();
        ctx.close();
    }
}
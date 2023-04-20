package com.aroasoft.core.tcpserver.websocket;

import com.aroasoft.core.tcpserver.tcpip.AROAEventListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.http.HttpObjectAggregator;
import io.netty.handler.codec.http.HttpServerCodec;
import io.netty.handler.codec.http.websocketx.extensions.compression.WebSocketServerCompressionHandler;
import io.netty.handler.ssl.SslContext;


public class WebSocketServerInitializer extends ChannelInitializer<SocketChannel> {
    private WebSocketEventListener webSocketEventListener;
    private final SslContext sslCtx;
    public WebSocketServerInitializer (SslContext sslCtx) {
        this.sslCtx = sslCtx;
    }


    public WebSocketServerInitializer() {
        sslCtx = null;
    }

    @Override
    public void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        if (sslCtx != null) {
            pipeline.addLast(sslCtx.newHandler(ch.alloc()));
        }
        pipeline.addLast(new HttpServerCodec());
        pipeline.addLast(new HttpObjectAggregator(65536));
        pipeline.addLast(new WebSocketServerCompressionHandler());
        pipeline.addLast(new WebSocketServerHandler(webSocketEventListener));
    }
    public void setEventListener(WebSocketEventListener webSocketEventListener) {
        this.webSocketEventListener = webSocketEventListener;
    }
}

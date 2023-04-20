package com.aroasoft.core.tcpserver.common;

import com.aroasoft.core.tcpserver.tcpip.AROAEventListener;
import com.aroasoft.core.tcpserver.tcpip.ServerAdapterInitializer;
import com.aroasoft.core.tcpserver.websocket.WebSocketEventListener;
import com.aroasoft.core.tcpserver.websocket.WebSocketServerInitializer;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import io.netty.handler.ssl.SslContext;
import io.netty.handler.ssl.SslContextBuilder;



import javax.net.ssl.SSLException;
import java.io.File;

public class ContainerServer {

    File cert = new File("/home/sslKey/www_metatourism.world.crt"); // 인증서 파일
    File key = new File("/home/sslKey/pkcs8_key.pem"); // 개인키 파일
   /* File key = new File("/home/sslKey/www_metatourism.world.pem"); // 개인키 파일*/
    SslContext sslCtx;
    /*public void start(int port, AROAEventListener aroaEventListener) throws Exception {
        EventLoopGroup producer = new NioEventLoopGroup(1);
        EventLoopGroup consumer = new NioEventLoopGroup();

        try {
            ServerAdapterInitializer serverAdapterInitializer = new ServerAdapterInitializer();
            serverAdapterInitializer.setEventListener(aroaEventListener);
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(producer, consumer)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(serverAdapterInitializer);

            System.out.println("Server started");

            //aroaEventListener.channels = serverAdapterInitializer.channels;
            bootstrap.bind(port).sync().channel().closeFuture().sync();

        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            producer.shutdownGracefully();
            consumer.shutdownGracefully();
        }
    }*/

    public void start(int port, WebSocketEventListener webSocketEventListener) throws Exception {
        EventLoopGroup bossGroup = new NioEventLoopGroup();
        EventLoopGroup workerGroup = new NioEventLoopGroup();
        try {
            /*SslContext.newServerContext(cert, key);*/
            /*  WebSocketServerInitializer webSocketServerInitializer = new WebSocketServerInitializer();*/
            sslCtx = SslContextBuilder.forServer(cert, key).build() ;
            WebSocketServerInitializer webSocketServerInitializer = new WebSocketServerInitializer(sslCtx);
            webSocketServerInitializer.setEventListener(webSocketEventListener);
            ServerBootstrap bootstrap = new ServerBootstrap()
                    .group(bossGroup, workerGroup)
                    .channel(NioServerSocketChannel.class)
                    .childHandler(webSocketServerInitializer);

            System.out.println("Server started");

            //aroaEventListener.channels = serverAdapterInitializer.channels;
            bootstrap.bind(port).sync().channel().closeFuture().sync();
        } finally {
            bossGroup.shutdownGracefully();
            workerGroup.shutdownGracefully();
        }
    }

}

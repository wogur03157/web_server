package com.aroasoft.core.tcpserver.tcpip;

import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.string.StringDecoder;
import io.netty.handler.codec.string.StringEncoder;

public class ServerAdapterInitializer extends ChannelInitializer<SocketChannel> {
    private AROAEventListener aroaEventListener;
    public ChannelGroup channels = null;
    @Override
    protected void initChannel(SocketChannel channel) throws Exception {
        ChannelPipeline pipeline = channel.pipeline();

        pipeline.addLast("decoder", new StringDecoder());
        pipeline.addLast("encoder", new StringEncoder());
        ServerAdapterHandler serverAdapterHandler = new ServerAdapterHandler(aroaEventListener);
        pipeline.addLast("handler", serverAdapterHandler);
        channels = serverAdapterHandler.channels;

    }
    public void setEventListener(AROAEventListener aroaEventListener)
    {
        this.aroaEventListener = aroaEventListener;
    }

}
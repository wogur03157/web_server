package com.aroasoft.core.tcpserver.websocket;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

public class WebSocketEventListener {
    public ChannelGroup channels = null;
    public ByteBuf onError()
    {
        return null;
    }
    public String onEvent1(String msg, ChannelHandlerContext ctx)
    {
        return null;
    };
}

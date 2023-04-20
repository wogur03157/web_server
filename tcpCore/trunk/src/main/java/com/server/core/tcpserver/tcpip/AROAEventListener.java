package com.aroasoft.core.tcpserver.tcpip;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.group.ChannelGroup;

public class AROAEventListener {
    public ChannelGroup channels = null;

    public ByteBuf onEvent(ByteBuf event)
    {
        return null;
    };
    public void onActive(ChannelHandlerContext ctx)
    {
        return;
    }
    public ByteBuf onError()
    {
        return null;
    }
    public String onEvent1(String msg, ChannelHandlerContext ctx)
    {
        return null;
    };
}

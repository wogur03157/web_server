package com.aroasoft.core.tcpserver.tcpip;

import java.nio.charset.Charset;

import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

public class TCPServerHandler extends ChannelInboundHandlerAdapter{
    private static final Charset CHARSET = Charset.forName("EUC_KR");

    private TCPCallBack writer;

    public TCPServerHandler(TCPCallBack write){
        this.writer = write;
    }

    //요청에 따른 읽기, 쓰기 이벤트 분기(인터페이스가 구현되어있지 않으면 에코)
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        ByteBuf inBuffer = (ByteBuf) msg;
        String received = inBuffer.toString(CHARSET);
        if(writer == null){
            System.out.println("Server received : " + received);
            ctx.write(Unpooled.copiedBuffer("echo : "+received, CHARSET));
        } else {
            writer.read(received);
            String arg = writer.write(received);
            ctx.write(Unpooled.copiedBuffer(arg.toCharArray(), CHARSET));
        }
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        ctx.writeAndFlush(Unpooled.EMPTY_BUFFER);
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        cause.printStackTrace();
        ctx.close();
    }

    //종료 이벤트
    @Override
    public void channelUnregistered(ChannelHandlerContext ctx) throws Exception {
        if(writer != null){
            writer.afterClose(ctx);
        }
        super.channelUnregistered(ctx);
    }
}

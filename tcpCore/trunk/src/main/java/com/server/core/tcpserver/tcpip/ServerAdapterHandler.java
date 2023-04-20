package com.aroasoft.core.tcpserver.tcpip;

import com.aroasoft.core.tcpserver.common.MessageUtilClass;
import com.aroasoft.core.tcpserver.common.UtilClass;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;

import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.group.ChannelGroup;
import io.netty.channel.group.DefaultChannelGroup;
import io.netty.util.concurrent.GlobalEventExecutor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ServerAdapterHandler extends ChannelInboundHandlerAdapter {
    private AROAEventListener aroaEventListener;
    private static final Logger logger = LoggerFactory.getLogger(ServerAdapterHandler.class);
    public static final ChannelGroup channels = new DefaultChannelGroup(
            "containers", GlobalEventExecutor.INSTANCE);
    public ServerAdapterHandler(AROAEventListener aroaEventListener)
    {
        this.aroaEventListener = aroaEventListener;
    }
    private boolean bBroadcast = false;
    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
       /* String strMsg = String.valueOf(msg);
        logger.debug("receive message \"{}\"", strMsg);
        ByteBuf byteBuf = UtilClass.strToBytoBuf(strMsg);
        ByteBuf sendMsg = aroaEventListener.onEvent1(byteBuf, ctx);
        String strRecvdMsg = MessageUtilClass.makeMessageHederLen(sendMsg);
        String broadcastMsg = "";
        if (strRecvdMsg != null && strRecvdMsg.length()>4) {
            String strTemp = "";
            String strBraoadHeader = strRecvdMsg.substring(11, 16);
            if (strBraoadHeader.equals("@all:")) {
                System.out.println("broadcast");
                bBroadcast = true;
                strTemp = strRecvdMsg.substring(16);
            }
            else {
                strTemp = strRecvdMsg;
                bBroadcast = false;
            }
            System.out.println(strTemp);
            sendMsg = UtilClass.strToBytoBuf(strTemp);
        }
        if (bBroadcast)
            channels.write(sendMsg);
        else
            ctx.write(sendMsg); // 버퍼에 쓰기
*/
    }

    @Override
    public void channelReadComplete(ChannelHandlerContext ctx) throws Exception {
        //ctx.flush(); // 채널 파이프라인에 저장된 버퍼 전송
        if (bBroadcast)
            channels.flush();
        else
            ctx.flush();
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {

        logger.error("exceptionCaught", cause);
        //ctx.close();
        ByteBuf sendMsg = aroaEventListener.onError();
        ByteBuf byteBuf = UtilClass.strToBytoBuf(MessageUtilClass.makeMessageHederLen(sendMsg));

        ctx.write(byteBuf);
        ctx.flush();
    }
    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        aroaEventListener.onActive(ctx);
        channels.add(ctx.channel());
    }

}
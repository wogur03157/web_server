package com.aroasoft.core.tcpserver.common;

import io.netty.buffer.ByteBuf;

public class MessageUtilClass {
    public static final int HEADER_BODY_LENGTH = 10;
    public static int readLen(ByteBuf event)
    {
        return Integer.parseInt(UtilClass.ByteBufToStr(event).substring(0,HEADER_BODY_LENGTH).replaceAll(" ",""));
    }
    public static String makeMessageHederLen(ByteBuf byteBuf)
    {
        String send = UtilClass.ByteBufToStr(byteBuf);
        int len = send.length();
        String strLen = UtilClass.MakeMessageLen(len);
        return strLen+","+send;
    }
}

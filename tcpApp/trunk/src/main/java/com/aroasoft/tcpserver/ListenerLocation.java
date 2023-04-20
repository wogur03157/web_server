package com.aroasoft.tcpserver;


import com.aroasoft.core.tcpserver.websocket.WebSocketEventHandler;
import com.aroasoft.core.tcpserver.websocket.WebSocketEventListener;
import com.aroasoft.tcpserver.mysql.query.LocationQuery;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;


//구현
public class ListenerLocation extends WebSocketEventListener {
    public ConnectUser connectUser;
    public  ListenerLocation() {

        WebSocketEventHandler.addListener(this);
        connectUser = ConnectUser.getInstance();
    }
    String id = "";
    JSONObject JsonObj= new JSONObject();
    @Override
    public String onEvent1(String event, ChannelHandlerContext ctx) {
        String msg = event;
        LocationQuery db = new LocationQuery();
        try {
            JSONObject json = new JSONObject(event);
            JSONObject header = json.getJSONObject("header");
            String broadcast = header.getString("broadcast");
            String opcode = header.getString("opcode");
            JSONObject body = new JSONObject();

            if(json.has("body")){
                body = json.getJSONObject("body");
                if(body.has("id")) {
                    id = body.getString("id");
                }
            }
            String sendMsg = "";
            if (opcode.equals("02003")) {
                String token="";
                if(body.has("token")){
                    token = body.getString("token");
                }
                sendMsg=getResult(id, token);
                System.out.println("sendMsg="+sendMsg);
            }else if (opcode.equals("01001")) {
                /*db.insPosition(event);*/
                sendMsg=event;
            }else {
                sendMsg = event;
            }
            if (broadcast.equals("1")) {
                msg = "@all:" + sendMsg;
            } else {
                msg = sendMsg;
            }
        } catch (Exception ex) {
            System.out.println(ex);
            ByteBuf byteBuf1 = Message.makeResponseMessage_Error();
            ctx.write(byteBuf1);
            ctx.flush();
            return null;
        }
        return msg;
    }
    public String getResult(String userid, String token) throws Exception{

        URI uri = new URI("wss://www.metatourism.world:12000/websocket?request=e2lkOjE7cmlkOjI2O3Rva2VuOiI0MzYwNjgxMWM3MzA1Y2NjNmFiYjJiZTExNjU3OWJmZCJ9");
        GetUserInfo getUserInfo = new GetUserInfo(uri);

        String STORETYPE = "JKS";
        String KEYSTORE = "/home/sslKey/sslcert.co.kr.jks";
        String STOREPASSWORD = "1q2w3e4r";
        String KEYPASSWORD = "1q2w3e4r";

        KeyStore ks = KeyStore.getInstance( STORETYPE );
        File kf = new File( KEYSTORE );
        ks.load( new FileInputStream( kf ), STOREPASSWORD.toCharArray() );

        KeyManagerFactory kmf = KeyManagerFactory.getInstance( "SunX509" );
        kmf.init( ks, KEYPASSWORD.toCharArray() );
        TrustManagerFactory tmf = TrustManagerFactory.getInstance( "SunX509" );
        tmf.init( ks );

        SSLContext sslContext = null;
        sslContext = SSLContext.getInstance( "TLS" );
        sslContext.init( kmf.getKeyManagers(), tmf.getTrustManagers(), null );

        SSLSocketFactory factory = sslContext.getSocketFactory();

        getUserInfo.setSocketFactory(factory);

        //웹소켓 커넥팅
        getUserInfo.connectBlocking();

        JSONObject result;

        String message = Message.returnToken_02003(userid,token);
        System.out.println("a");
        //웹소켓 메세지 보내기
        getUserInfo.send(message);
        System.out.println("b");
        Thread.sleep(1000);
        JsonObj=getUserInfo.getResult();
        if(JsonObj.has(id)){
            if(JsonObj.getString(id).equals(token)){
                message = Message.returnToken_02003(userid,token);
            }
        }
        return message;
    }

    @Override
    public ByteBuf onError() {
        ByteBuf byteBuf = Message.makeResponseMessage_Error();
        return byteBuf;
    }
}

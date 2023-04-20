package com.aroasoft.tcpserver;


import com.aroasoft.core.tcpserver.websocket.WebSocketEventHandler;
import com.aroasoft.core.tcpserver.websocket.WebSocketEventListener;
import com.aroasoft.tcpserver.mysql.query.BusinessQuery;
import com.aroasoft.tcpserver.mysql.query.DBQueryTest;
import com.google.protobuf.StringValue;
import com.google.protobuf.Value;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONArray;
import org.json.JSONObject;

import javax.net.ssl.KeyManagerFactory;
import javax.net.ssl.SSLContext;
import javax.net.ssl.SSLSocketFactory;
import javax.net.ssl.TrustManagerFactory;
import java.io.File;
import java.io.FileInputStream;
import java.net.URI;
import java.security.KeyStore;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;


//구현
public class ListenerBusiness extends WebSocketEventListener {
    public ConnectUser connectUser;
    public ListenerBusiness() {

        WebSocketEventHandler.addListener(this);
        connectUser = ConnectUser.getInstance();
    }
    String id = "";
    JSONObject JsonObj= new JSONObject();
    @Override
    public String onEvent1(String event, ChannelHandlerContext ctx) {
        String msg = event;
        BusinessQuery db = new BusinessQuery();
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
            } else if (opcode.equals("02005")) {
                String point = body.getString("point");
                String rtnVal= String.valueOf(db.updPoint(id,point));
                sendMsg=Message.returnUpdatePoint_02005(id, rtnVal);
                System.out.println("sendMsg="+sendMsg);
            } else if (opcode.equals("01001")) {
                int status=1;
                int rtnVal= db.insPosition(event);
                if (rtnVal>0) status=1;
                else status = 0;
                sendMsg=event;
            }
            else if (opcode.equals("05100")) {
                int rtnVal=1;
                int status=1;
                rtnVal= db.insItem(event);
                if (rtnVal>0) status=1;
                else status = 0;
                sendMsg=Message.makeResposeMessage_05100(String.valueOf(status));
            } else if (opcode.equals("05101")) {
                int rtnVal=1;
                int status=1;
                rtnVal= db.delItem(event);
                if (rtnVal>0) status=1;
                else status = 0;
                sendMsg=Message.makeResposeMessage_05101(String.valueOf(status));
            }else if (opcode.equals("05102")) {
                JSONObject rtnVal= db.getItemList(event);
                sendMsg=Message.makeResponseMessage_05102(rtnVal);
            }else if (opcode.equals("05005")) {
                String id = body.getString("id");
                String token = body.getString("token");
                String price = body.getString("price");
                JSONObject codes = body.getJSONObject("item");
                String acode = codes.getString("acode");
                String bcode = codes.getString("bcode");
                String ccode = codes.getString("ccode");

                HashMap map =  db.purchaseItem(id, price, acode, bcode, ccode, token);
                String purchaseItemSendMsg=Message.returnPurchaseItem_05005(map);
                System.out.println("purchaseItem="+purchaseItemSendMsg);
                sendMsg = purchaseItemSendMsg;

            }else if (opcode.equals("05007")){
                JSONObject rtn = db.getRoomList();
                sendMsg =Message.makeResponseMessage_05007(rtn);

            }else if(opcode.equals("05004")){
                String rm_seq = body.getString("seq");
                JSONObject roomInfo = db.getRoomInfo(rm_seq);
                sendMsg = Message.makeResponseMessage_05004(roomInfo);

            }else if(opcode.equals("05002")){
                String id= body.getString("mem_id");
                String rm_seq= body.getString("seq");
                String rm_id= body.getString("rm_id");
                int res = db.userEnterRoom(rm_seq,id,rm_id);
                sendMsg = Message.makeResponseMessage_05002(res);

            }else if(opcode.equals("05008")){
                String txt= body.getString("searchTxt");
                JSONArray seqList = db.searchRoom(txt);
                sendMsg = Message.makeResponseMessage_05008(seqList);

            }else if(opcode.equals("05020")){
                String id= body.getString("mem_id");
                String rm_seq= body.getString("seq");
                String rm_id= body.getString("rm_id");
                db.userQuitRoom(rm_seq,id,rm_id);

            }else if (opcode.equals("05006")) {
                List<JSONObject> db_map = db.getAllItems(id);
                sendMsg = Message.makeResposeMessage_05006(db_map);
                // 장착 수정,추가
            }else if (opcode.equals("05010")) {
                body = json.getJSONObject("body");
                String bcode = body.getString("bcode");
                String ccode = body.getString("c_value");
                int count = db.invenInfo(id, bcode, ccode);
                if(count == 0){
                    db.ins_memcinfo(id,bcode,ccode);
                }else{
                    db.upd_memcinfo(id,bcode,ccode);
                }
                List<JSONObject> db_map = db.getAllItems(id);
                sendMsg = Message.makeResposeMessage_05006(db_map);

                // 장착 삭제
            }else if (opcode.equals("05011")) {
                body = json.getJSONObject("body");
                String bcode = body.getString("bcode");
                String ccode = body.getString("c_value");
                db.del_memcinfo(id, bcode, ccode);
                List<JSONObject> db_map = db.getAllItems(id);
                sendMsg = Message.makeResposeMessage_05006(db_map);

            }else if (opcode.equals("05016")) {
                List<JSONObject> db_map = db.inven_furniture(id);
                sendMsg = Message.makeResposeMessage_05016(db_map);
            }else if (opcode.equals("05017")) {
                List<JSONObject> db_map = db.inven_furniture(id);
                sendMsg = Message.makeResposeMessage_05017(db_map);
            }else if (opcode.equals("05030")) {
                List<JSONObject> db_map = db.getAllItems(id);
                sendMsg = Message.makeResposeMessage_05030(db_map);
            } else {
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

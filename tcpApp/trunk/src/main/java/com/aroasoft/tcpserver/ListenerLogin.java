package com.aroasoft.tcpserver;

import com.aroasoft.core.tcpserver.common.UtilClass;
import com.aroasoft.core.tcpserver.websocket.WebSocketEventHandler;
import com.aroasoft.core.tcpserver.websocket.WebSocketEventListener;
import com.aroasoft.tcpserver.mysql.query.DBQueryTest;
import com.aroasoft.tcpserver.mysql.query.MemberQuery;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import org.json.JSONObject;

import javax.activation.DataHandler;
import javax.activation.DataSource;
import javax.activation.FileDataSource;
import javax.mail.*;
import javax.mail.internet.*;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.stream.Collectors;

//구현
public class ListenerLogin extends WebSocketEventListener {
    public ConnectUser connectUser;
    public ConnectLobby connectLobby;
    public ConnectEmart connectEmart;
    public ListenerLogin() {

        WebSocketEventHandler.addListener(this);
        connectUser = ConnectUser.getInstance();
        connectLobby = connectLobby.getInstance();
        connectEmart = ConnectEmart.getInstance();
    }
    String id = "";
    @Override
    public String onEvent1(String event, ChannelHandlerContext ctx) {

        String msg = event;
        DBQueryTest db = new DBQueryTest();
        try {
            JSONObject json = new JSONObject(event);
            JSONObject header = json.getJSONObject("header");
            String broadcast = header.getString("broadcast");
            String opcode = header.getString("opcode");
            JSONObject body = new JSONObject();

            if (json.has("body")) {
                body = json.getJSONObject("body");
                if (body.has("id")) {
                    id = body.getString("id");
                }
            }
            String sendMsg = "";
       /* else if (opcode.equals("01006")) {
                String time = body.getString("time");
                db.inscoordinate(id, header, body, time);
            } else if (opcode.equals("01007")) {
                db.updateInfo(id, header, body);
            } else if (opcode.equals("01008")) {
                String time = body.getString("time");
                db.updatePing(id, time);
            }*/
            if (opcode.equals("02001")) {
                MemberQuery memberQuery = new MemberQuery();
                String pwd = body.getString("password");
                String pwd_sha2 = UtilClass.SHA256(pwd);
                String token = UtilClass.SHA256(UtilClass.getNowMilSendFormat() + "_" + UtilClass.getUUID());
                String name = "";
                int chracterChk = 0;
                String rmSeq = "0";

                int status = 1;
                System.out.println(id + ", " + pwd_sha2);
                HashMap map = memberQuery.userLogin(id, pwd_sha2);
                System.out.println("2002");
                int rtnVal =Integer.parseInt(map.get("rtnVal").toString());
                if (rtnVal>0) {
                    token = "";
                    sendMsg = Message.returnLoginInfo_02001(id, token, String.valueOf(status), "", "", "", 1, "", "","");
                    return sendMsg;
                }
                else {
                    status = 0;
                    chracterChk = memberQuery.characterCheck(id);
                     /*  rtnVal=memberQuery.insertToken(id,token);
                if (rtnVal>0)
                    token = "";

                else status = 0;*/
                }
                if(map.get("name")!= null){
                    name = map.get("name").toString();
                }
                String pattern = "yyyyMMddhhmmss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());
                memberQuery.updTime(id,date);
                memberQuery.updateMemState("1",id);
                connectUser.vUser.add(id);
                sendMsg = Message.returnLoginInfo_02001(id, token, String.valueOf(status), map.get("point").toString(), map.get("pos").toString(), name, chracterChk, memberQuery.getCharacter(id),map.get("rmSeq").toString(),map.get("addr").toString());
            } else if (opcode.equals("01003")) {
                MemberQuery memberQuery = new MemberQuery();
                String type =body.getString("type");
                if(type.equals("park")) {
                    if( !connectUser.vUser.contains(id)){
                        connectUser.vUser.add(id);
                    }
                    if(connectLobby.LobbyUser.contains(id)){
                        connectLobby.LobbyUser.remove(id);
                    }
                    if (connectEmart.EmartUser.contains(id)) {
                        connectEmart.EmartUser.remove(id);
                    }
                    List<Object> list = Arrays.asList(connectUser.vUser.toArray()).stream().distinct().collect(Collectors.toList());
                    String strList = Arrays.toString(list.toArray());
                    sendMsg = Message.makeResposeMessage_01003(strList);
                } else if(type.equals("lobby")) {
                    if (!connectLobby.LobbyUser.contains(id)) {
                        connectLobby.LobbyUser.add(id);
                    }
                    if (connectUser.vUser.contains(id)) {
                        connectUser.vUser.remove(id);
                    }
                    if (connectEmart.EmartUser.contains(id)) {
                        connectEmart.EmartUser.remove(id);
                    }
                    List<Object> list = Arrays.asList(connectLobby.LobbyUser.toArray()).stream().distinct().collect(Collectors.toList());
                    String strList = Arrays.toString(list.toArray());
                    sendMsg = Message.makeResposeMessage_01003(strList);
                }else if(type.equals("Emart")) {
                    if (!connectEmart.EmartUser.contains(id)) {
                        connectEmart.EmartUser.add(id);
                    }
                    if (connectLobby.LobbyUser.contains(id)) {
                        connectLobby.LobbyUser.remove(id);
                    }
                    if (connectUser.vUser.contains(id)) {
                        connectUser.vUser.remove(id);
                    }
                    List<Object> list = Arrays.asList(connectEmart.EmartUser.toArray()).stream().distinct().collect(Collectors.toList());
                    String strList = Arrays.toString(list.toArray());
                    sendMsg = Message.makeResposeMessage_01003(strList);
                }
            }else if (opcode.equals("02010")) {
                int status = 1;
                HashMap hashMap = new HashMap();
                hashMap.put("id", id);
                Random rand = new Random();
                String numStr = ""; //난수가 저장될 변수
                for(int i=0;i<6;i++) {

                    //0~9 까지 난수 생성
                    String ran = Integer.toString(rand.nextInt(10));

                    numStr += ran;
                }
                String pwd_sha2 = UtilClass.SHA256(numStr);
                MemberQuery memberQuery = new MemberQuery();
                status= memberQuery.updatePwd( id, pwd_sha2);
                String email=memberQuery.getUserInfo(id);
                gmailSend(email,numStr);
                status = 0;

                sendMsg  = Message.returnEmail_02010(email, String.valueOf(status));
            }else if (opcode.equals("02002")) {
                int status = 1;
                String pwd = body.getString("password");
                String pwd_sha2 = UtilClass.SHA256(pwd);
                String email = body.getString("email");
                //String username = body.getString("username");
                HashMap hashMap = new HashMap();
                hashMap.put("id", id);
                hashMap.put("pwd", pwd_sha2);
                //hashMap.put("name", username);
                hashMap.put("email", email);
                MemberQuery memberQuery = new MemberQuery();
                if (memberQuery.idCheck(id,email) > 0) {
                    status = 1;
                } else {
                    memberQuery.insertUser(hashMap);
                    status = 0;
                }
                sendMsg  = Message.returnRegInfo_02002(id, String.valueOf(status));
            } else if (opcode.equals("02003")) {
                int status = 1;
                MemberQuery memberQuery = new MemberQuery();
                HashMap map = memberQuery.tokenCheck(id);
                int rtnVal = Integer.parseInt(map.get("rtnVal").toString());
                String token = "";
                if (rtnVal > 0)
                    token = map.get("token").toString();

                else status = 0;
                sendMsg = Message.returnToken_02003(id, token);

            } else if (opcode.equals("02005")) {
                int status = 1;
                MemberQuery memberQuery = new MemberQuery();
                String pattern = "yyyyMMddhhmmss";
                SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern);
                String date = simpleDateFormat.format(new Date());
                int rtnVal =memberQuery.AliveCheck(date,id);
                sendMsg = Message.returnToken_02005(id, String.valueOf(rtnVal));
            }else if (opcode.equals("02004")) {
                int status = 1;
                MemberQuery memberQuery = new MemberQuery();
                String token = body.getString("token");
                int rtnVal = memberQuery.DelToken(token);
                if (rtnVal > 0)
                    token = "";

                else status = 0;


                if(connectUser.vUser.contains(id)){
                    connectUser.vUser.remove(id);
                }
                if(connectLobby.LobbyUser.contains(id)){
                    connectLobby.LobbyUser.remove(id);
                }
                if(connectEmart.EmartUser.contains(id)){
                    connectEmart.EmartUser.remove(id);
                }
                memberQuery.updateMemState("0",id);
                sendMsg=Message.returnLogout_02004(id,String.valueOf(status));
            }else if (opcode.equals("05001")) {
                int status = 0;
                MemberQuery memberQuery = new MemberQuery();
                JSONObject character = body.getJSONObject("character_value");

                HashMap map = new HashMap();
                map.put("id", body.getString("id"));
                map.put("name", character.getString("name"));
                int rtnVal=0;
                rtnVal=memberQuery.nameCheck(character.getString("name"));
                if (rtnVal>0) {
                    sendMsg = Message.makeResponseMessage_error(  "05001","2",body.getString("id"));
                    return sendMsg;
                }
                memberQuery.updateUserName(map);
                map.put("code", "001");
                map.put("value", character.getInt("skin"));
                memberQuery.insertCharacter(map);
                map.put("code", "002");
                map.put("value", character.getInt("body"));
                memberQuery.insertCharacter(map);
                map.put("code", "003");
                map.put("value", character.getInt("hair"));
                memberQuery.insertCharacter(map);
                //map.put("code", "004");
               /* //map.put("value", character.getInt("hairColor"));
                map.put("value", 0);
                memberQuery.insertCharacter(map);*/
                map.put("code", "005");
                map.put("value", -1);
                memberQuery.insertCharacter(map);
                map.put("code", "006");
                map.put("value", -1);
                memberQuery.insertCharacter(map);


                status = 1;

                sendMsg = Message.returnRegInfo_05001(id, String.valueOf(status), character);
            }else if (opcode.equals("05022")) {
                int status = 0;
                String userid="";
                if (json.has("body")) {
                    body = json.getJSONObject("body");
                    if (body.has("id")) {
                        userid = body.getString("id");
                    }
                }
                MemberQuery memberQuery = new MemberQuery();

                sendMsg = Message.returnMemInfo_05022(memberQuery.getCharacter(userid),userid);
            }else if (opcode.equals("02007")) {
                int status = 1;
                String token = body.getString("token");
                String type =body.getString("type");

                if(type=="1") {
                    if( !connectUser.vUser.contains(id)){
                        connectUser.vUser.add(id);
                        if(connectLobby.LobbyUser.contains(id)){
                            connectLobby.LobbyUser.remove(id);
                        }
                    }
                }
                if(type=="2") {
                    if( !connectLobby.LobbyUser.contains(id)){
                        connectLobby.LobbyUser.add(id);
                        if(connectUser.vUser.contains(id)){
                            connectUser.vUser.remove(id);
                        }
                    }
                }
                sendMsg=Message.returnLogout_02004(id,String.valueOf(status));

            }else if(opcode.equals("04001")) {
                HashMap map = new HashMap();
                map.put("address", body.getString("address"));
                map.put("id", id);
                MemberQuery memberQuery = new MemberQuery();
                memberQuery.updateAddress(map);
            }else if (opcode.equals("05005")) {
                MemberQuery memberQuery = new MemberQuery();
                String id = body.getString("id");
                String token = body.getString("token");
                String price = body.getString("price");
                JSONObject codes = body.getJSONObject("item");
                String acode = codes.getString("acode");
                String bcode = codes.getString("bcode");
                String ccode = codes.getString("ccode");

                HashMap map = memberQuery.purchaseItem(id, price, acode, bcode, ccode, token);
                sendMsg = Message.returnPurchaseItem_05005(map);
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
    @Override
    public ByteBuf onError() {
        ByteBuf byteBuf = Message.makeResponseMessage_Error();
        return byteBuf;
    }

    public static void gmailSend(String email,String pwd) {
        String user = "metatourismpm@gmail.com"; // 네이버일 경우 네이버 계정, gmail경우 gmail 계정
        String password = "vfkyxqnkaoxqwvdd"; // 패스워드

        // SMTP 서버 정보를 설정한다.
        Properties prop = new Properties();
        prop.put("mail.smtp.host", "smtp.gmail.com");
        prop.put("mail.smtp.port", 465);
        prop.put("mail.smtp.auth", "true");
        prop.put("mail.smtp.ssl.enable", "true");
        prop.put("mail.smtp.ssl.trust", "smtp.gmail.com");

        Session session = Session.getDefaultInstance(prop, new javax.mail.Authenticator() {
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(user, password);
            }
        });

        try {
            MimeMessage message = new MimeMessage(session);
            message.setFrom(new InternetAddress(user));            //수신자메일주소
            message.addRecipient(javax.mail.Message.RecipientType.TO, new InternetAddress(email));
            MimeMultipart mimeMultipart = new MimeMultipart("related");
            // Subject
            message.setSubject("비밀번호 찾기"); //메일 제목을 입력

            BodyPart messageBodyPart = new MimeBodyPart();
            String htmlText = "<img src=\"cid:logo-image\">\r\n"
                    +"<br><br>"
                    +"<span>비밀번호 :"+pwd+"</span>\r\n"
                    +"<br><br>"
                    +"<div>● 자동메세지 입니다. 회신하지 마십시오.</div>\r\n"
                    +"<div>● 인증번호를 다른 사람과 공유하지 마십시오.</div>\r\n"
                    +"<br><br>"
                    +"<td width=\"600\" height=\"80\" align=\"center\" colspan=\"3\" bgcolor=\"#4b4b4b\" style=\"font-size:0px\"><span style=\"font-family:&quot;Nanum Gothic&quot;,&quot;Malgun Gothic&quot;,dotum,AppleGothic,Helvetica,Arial,sans-serif;font-size:14px;color:rgb(153,153,153);line-height:1.6;letter-spacing:-1.25px;text-align:left\">COPYRIGHT© METATOURISM. ALL RIGHTS RESERVED.</span></td>";
            messageBodyPart.setContent(htmlText, "text/html; charset=UTF-8");

            // add it
            mimeMultipart.addBodyPart(messageBodyPart);

            // second part (the image)
            messageBodyPart = new MimeBodyPart();
            String filePath = "/root/tcpApp/img/meta.jpg";

            /* src/main/java/com/aroasoft/tcpserver/meta.jpg*/

            DataSource fds = new FileDataSource(filePath);
            messageBodyPart.setDataHandler(new DataHandler(fds));
            messageBodyPart.setHeader("Content-ID","<logo-image>");
            // add it
            mimeMultipart.addBodyPart(messageBodyPart);

            // put everything together
            message.setContent(mimeMultipart);
            // Text
          /*  message.setContent("<img src=\"cid:logo-image\">\r\n"
                    +"<br><br>"
                    +"<span>비밀번호 :"+pwd+"</span>\r\n"
                    +"<br><br>"
                    +"<div>● 자동메세지 입니다. 회신하지 마십시오.</div>\r\n"
                    +"<div>● 인증번호를 다른 사람과 공유하지 마십시오.</div>\r\n"
                    +"<br><br>"
                    +"<td width=\"600\" height=\"80\" align=\"center\" colspan=\"3\" bgcolor=\"#4b4b4b\" style=\"font-size:0px\"><span style=\"font-family:&quot;Nanum Gothic&quot;,&quot;Malgun Gothic&quot;,dotum,AppleGothic,Helvetica,Arial,sans-serif;font-size:14px;color:rgb(153,153,153);line-height:1.6;letter-spacing:-1.25px;text-align:left\">COPYRIGHT© METATOURISM. ALL RIGHTS RESERVED.</span></td>" ,"text/html; charset=UTF-8");    //메일 내용을 입력
*/
            // send the message

            Transport.send(message); ////전송
            System.out.println("message sent successfully...");
        } catch (AddressException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (MessagingException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
}

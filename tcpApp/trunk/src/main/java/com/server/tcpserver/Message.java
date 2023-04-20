package com.aroasoft.tcpserver;

import com.aroasoft.core.tcpserver.common.UtilClass;
import io.netty.buffer.ByteBuf;
import org.json.JSONArray;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;

public class Message {


    public static String makeResposeMessage_01003(String strList)
    {
        String send = "{'header': {'broadcast':'0','opcode': '01003'} ,'body':{ 'id':'"+strList+"'}}";
        return send;
    }
    public static String makeResposeMessage_05100(String strList)
    {
        String send = "{'header': {'broadcast':'0','opcode': '01003'} ,'body':{ 'status':'"+strList+"'}}";
        return send;
    }
    public static String makeResposeMessage_05101(String strList)
    {
        String send = "{'header': {'broadcast':'0','opcode': '01003'} ,'body':{ 'status':'"+strList+"'}}";
        return send;
    }
    public static String makeResposeMessage_03001(String strList,String list)
    {
        String send = "{'header': {'broadcast':'0','opcode': '03001'} ,'body':{ 'status':'"+strList+"','id':'"+list+"'}}";
        return send;
    }
    public static String returnLoginInfo_02001(String id,String token, String status, String point, String pos, String name, int charChk, String character,String rmSeq,String addr)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02001'}, 'body':{'id':'"+id+"','token':'"+token+"'," +
                "'status':'"+status+"','point' : '"+point+"','pos':'"+pos+"', 'name':'"+name+"', 'charChk':'"+charChk+"', 'character': '"+character+"', 'rmSeq': '"+rmSeq+"', 'addr': '"+addr+"'} }";
        return send;
    }

    public static String returnToken_02003(String id,String token)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02003'}, 'body':{'id':'"+id+"','token':'"+token+"'} }";
        return send;
    }
    public static String returnToken_02005(String id,String status)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02005'}, 'body':{'id':'"+id+"','status':'"+status+"'} }";
        return send;
    }
    public static String returnUpdatePoint_02005(String id, String status)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02005'}, 'body':{'id':'"+id+"','status':'"+status+"'} }";
        return send;
    }
    public static String returnLogout_02004(String id, String status)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02004'}, 'body':{'id':'"+id+"','status':'"+status+"'} }";
        return send;
    }
    public static ByteBuf makeResponseMessage_Error()
    {
        String strData = "92        ,{\"header\" : {\"opcode\":\"99001\"},\"body\" : {\"error\":\"System failure. Please try again later.\"}}";
        ByteBuf byteBuf = UtilClass.strToBytoBuf(strData);
        return byteBuf;
    }

    public static String returnRegInfo_02002(String id,String status)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02002'}, 'body':{'id':'"+id+"','status':'"+status+"'} }";
        return send;
    }
    public static String returnEmail_02010(String id,String status)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '02010'}, 'body':{'id':'"+id+"','status':'"+status+"'} }";
        return send;
    }

    public static String returnRegInfo_05001(String id,String status, JSONObject character)
    {
        String send = "{'header': {'broadcast':'0', 'opcode': '05001'}, 'body':{'id':'"+id+"','status':'"+status+"', 'character':'"+character.toString()+"'} }";
        return send;
    }

    public static String returnPurchaseItem_05005(HashMap map)
    {
        String result =  map.get("result").toString();
        String token =  map.get("token").toString();
        String id =  map.get("id").toString();

        String send = "{'header': {'broadcast':'0', 'opcode': '05005'}, 'body':{'id':'"+id+"','token':'"+token+"','status':'"+result+"'} }";

        return send;
    }
    public static String makeResposeMessage_05006(List<JSONObject> map)
    {
        String data ="{";
        String chk =",";
        for (int i=0; i<map.size();i++){
            if(i == map.size()-1){
                chk="}";
            }
            JSONObject obj = map.get(i);
            data += "item"+i+":"+obj.toString()+chk;
        }

        String send = "{'header': {'opcode': '05006' , 'size' : '"+map.size()+"','itemInfo':'"+data+"'}}";
        System.out.println("getitemlist"+send);
        return send;
    }


    public static String makeResponseMessage_05007(JSONObject rtnVal)
    {
        String strData = "{'header' : {'opcode':'05007'},'body' : {'roomList':"+rtnVal.getJSONArray("roomList")+", 'maxFloor':'"+ rtnVal.getInt("max_floor")+"'}}";
        System.out.println("in : "+strData);
        return strData;
    }
    public static String makeResponseMessage_05102(JSONObject rtnVal)
    {
        String strData = "{'header' : {'opcode':'05102'},'body' : {'itemList':"+rtnVal.getJSONArray("itemList").toString().replaceAll("\"","\'" +
                "")+"}}";
        System.out.println("in : "+strData);
        return strData;
    }

    public static String makeResponseMessage_05002(int result)
    {
        String strData = "{'header' : {'opcode':'05002'},'body' : {'result':"+result+"}}";

        return strData;
    }
    public static String makeResponseMessage_05004(JSONObject rtnVal)
    {
        String str="";
        if(rtnVal.has("memList")){
            str=rtnVal.getJSONArray("memList").toString();
        }
        String strData = "{'header' : {'opcode':'05004'},'body' : {'roomInfo':"+rtnVal.getJSONObject("roomInfo")+", 'memList':'"+ str+"'}}";

        return strData;
    }
    public static String makeResponseMessage_05008(JSONArray rtnVal)
    {
        String strData = "{'header' : {'opcode':'05008'},'body' : {'seqList':"+rtnVal+"}}";

        return strData;
    }
    public static String makeResposeMessage_05016(List<JSONObject> map)
    {
        String data ="{";
        String chk =",";
        for (int i=0; i<map.size();i++){
            if(i == map.size()-1){
                chk="}";
            }
            JSONObject obj = map.get(i);
            data += "item"+i+":"+obj.toString()+chk;
        }

        String send = "{'header': {'opcode': '05016' , 'size' : '"+map.size()+"','itemInfo':'"+data+"'}}";
        System.out.println("furniture_list "+send);
        return send;
    }

    public static String makeResposeMessage_05017(List<JSONObject> map)
    {
        String data ="{";
        String chk =",";
        for (int i=0; i<map.size();i++){
            if(i == map.size()-1){
                chk="}";
            }
            JSONObject obj = map.get(i);
            data += "item"+i+":"+obj.toString()+chk;
        }

        String send = "{'header': {'opcode': '05017' , 'size' : '"+map.size()+"','itemInfo':'"+data+"'}}";
        return send;
    }
    public static String returnMemInfo_05022(String character,String id)
    {
        String send="{'header':{'broadcast':'0', 'opcode':'05022'}, 'body':{'id':'"+id+"', 'character':'"+character+"'}}";
        return send;
    }

    public static String makeResposeMessage_05030(List<JSONObject> map)
    {
        String data ="{";
        String chk =",";
        for (int i=0; i<map.size();i++){
            if(i == map.size()-1){
                chk="}";
            }
            JSONObject obj = map.get(i);
            data += "item"+i+":"+obj.toString()+chk;
        }

        String send = "{'header': {'opcode': '05030' , 'size' : '"+map.size()+"','itemInfo':'"+data+"'}}";
        System.out.println("getitemlist"+send);
        return send;
    }

    public static String makeResponseMessage_error(String opcode,String state,String id)
    {


        String send = "{'header': {'broadcast':'0', 'opcode': '"+opcode+"'}, 'body':{'id':'"+id+"','status':'"+state+"'} }";

        return send;
    }
}

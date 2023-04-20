package com.aroasoft.tcpserver.mysql.query;

import com.aroasoft.tcpserver.mysql.DBManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class BusinessQuery {

    public int updPoint(String id,String point) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;

        String query = "update member set mem_point='"+point+"' " +
                "where mem_id ='"+id+"';";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        st.close();
        rtnVal = 0;
        dbManager.close();
        return rtnVal;
    }

    public int insPosition (String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        JSONObject json=new JSONObject(str);
        JSONObject header=json.getJSONObject("header");
        JSONObject body=json.getJSONObject("body");
        String pos=body.toString();
        String id=body.getString("id");

        String query = "insert into member_position(mem_seq, memp_position) " +
                "select mem_seq,'"+pos+"'" +
                "from member " +
                "where mem_id = '"+id+"' " +
                "ON DUPLICATE KEY UPDATE " +
                "memp_position ='"+pos+"';";
        Statement st = dbManager.conn.createStatement();
        System.out.println(query);
        st.execute(query);
        rtnVal=0;
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public int insItem (String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        JSONObject json=new JSONObject(str);
        JSONObject header=json.getJSONObject("header");
        JSONObject body=json.getJSONObject("body");
        String pos=body.toString();
        String id=body.getString("id");
        String item=body.getString("item");
        String seq=body.getString("room");

        String query = "insert into room_item(rm_seq,mem_seq,rmi_bcode,rmi_code, rmi_position) " +
                "VALUES( '"+seq+"',(select mem_seq from member where mem_id ='"+id+"'),'009','"+item+"','"+pos+"');";
        Statement st = dbManager.conn.createStatement();
        System.out.println(query);
        st.execute(query);
        rtnVal=0;
        st.close();
        dbManager.close();
        return rtnVal;
    }

    public int delItem (String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        JSONObject json=new JSONObject(str);
        JSONObject header=json.getJSONObject("header");
        JSONObject body=json.getJSONObject("body");
        String id=body.getString("id");
        String seq=body.getString("room");

        String query = "DELETE FROM room_item " +
                "WHERE  rm_seq= '"+seq+"' and  mem_seq=(select mem_seq from member where mem_id ='"+id+"');";
        Statement st = dbManager.conn.createStatement();
        System.out.println(query);
        st.execute(query);
        rtnVal=0;
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public JSONObject getItemList(String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        JSONObject rtnVal = new JSONObject();
        ResultSet rs = null;
        JSONObject json=new JSONObject(str);
        JSONObject header=json.getJSONObject("header");
        JSONObject body=json.getJSONObject("body");
        String id=body.getString("id");
        String seq=body.getString("room");
        JSONArray roomList = new JSONArray();
        String query = "select rmi_code,rmi_position from room_item " +
                "where rm_seq = '"+seq+"';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while (rs.next()) {
            JSONObject jsonObj = new JSONObject();

            jsonObj.put("seq", rs.getString("rmi_code"));
            jsonObj.put("pos", rs.getString("rmi_position"));

            roomList.put(jsonObj);
        }
        rtnVal.put("itemList", roomList);
        dbManager.close();
        return rtnVal;
    }
    public JSONObject getRoomList() throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;
        JSONObject rtnVal = new JSONObject();
        Statement st = dbManager.conn.createStatement();

        // 방 멤버 중 비 접속 아이디 삭제
        String query = "delete from " +
                " room_inmember r " +
                " where exists(" +
                " select 1" +
                " from metatourism.member m " +
                " where r.mem_id = m.mem_id  and m.mem_state = 0)";
        st.execute(query);

        query = "select max(rm_floor) from room";

        rs = st.executeQuery(query);
        if (rs.next()) {
            rtnVal.put("max_floor", rs.getInt("max(rm_floor)"));
        }
        JSONArray roomList = new JSONArray();
        query = "select s.* ," +
                "   (select count(distinct r.mem_id) " +
                "      from metatourism.room_inmember r " +
                "        left join metatourism.member m    " +
                "       on r.mem_id = m.mem_id  " +
                "        where r.rm_seq = s.rm_seq and m.mem_state != 0) as user_count" +
                " from " +
                "(select a.rm_seq as rm_seq,a.mem_seq,a.rm_floor,a.rm_roomnumber,a.rm_nftvalue,a.rm_grade,a.rm_id,a.rm_name,b.mem_id " +
                "   from room a" +
                "   left join metatourism.member b " +
                "    on a.mem_seq = b.mem_seq) s ";
        st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while (rs.next()) {
            JSONObject json = new JSONObject();

            json.put("seq", rs.getInt("rm_seq"));
            json.put("mem_seq", rs.getInt("mem_seq"));
            json.put("floor", rs.getInt("rm_floor"));
            json.put("number", rs.getInt("rm_roomnumber"));
            json.put("nftvalue", rs.getString("rm_nftvalue"));
            json.put("grade", rs.getString("rm_grade"));
            json.put("id", rs.getString("rm_id"));
            json.put("name", rs.getString("rm_name"));
            json.put("user_count", rs.getString("user_count"));
            json.put("user_name", rs.getString("mem_id"));
            roomList.put(json);
        }
        rtnVal.put("roomList", roomList);
        dbManager.close();
        return rtnVal;
    }

    public JSONObject getRoomInfo(String rm_seq) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        JSONObject rtn = new JSONObject();

        ResultSet rs = null;
        Statement st = dbManager.conn.createStatement();

        String query = "select count(distinct r.mem_id) " +
                "      from metatourism.room_inmember r " +
                "        left join metatourism.member m    " +
                "      on r.mem_id = m.mem_id  " +
                "        where r.rm_seq = '"+rm_seq+"' and m.mem_state != 0";
        rs = st.executeQuery(query);

        int count = 0;
        if (rs.next()){
            count = rs.getInt(1);
        }
        if (count > 0) {
            query = "select r.mem_id from room_inmember r " +
                    "left join member m  " +
                    "on m.mem_id = r.mem_id " +
                    "where rm_seq ='"+rm_seq+"' and m.mem_state !=0 group by m.mem_id";
            rs = st.executeQuery(query);

            ArrayList memList = new ArrayList();
            while (rs.next()) {
                memList.add(rs.getString("mem_id"));
            }
            rtn.put("memList", memList);
        }
        query = "select rm_seq,rm_grade,rm_id,rm_name,rm_nftvalue from room where rm_seq = '"+rm_seq+"'";
        rs = st.executeQuery(query);
        JSONObject json = new JSONObject();
        if (rs.next()) {
            json.put("seq", rs.getString("rm_seq"));
            json.put("grade", rs.getString("rm_grade"));
            json.put("id", rs.getString("rm_id"));
            json.put("name", rs.getString("rm_name"));
            json.put("nft", rs.getString("rm_nftvalue"));
        }
        rtn.put("roomInfo", json);
        dbManager.close();
        return rtn;
    }

    public int userEnterRoom(String rm_seq, String user_id, String rm_id) throws Exception {
        DBManager dbManager = new DBManager();

        dbManager.initConnection();
        ResultSet rs = null;
        Statement st = dbManager.conn.createStatement();
        //  방 멤버에 유저 추가
        String query = "insert into room_inmember (rm_seq,mem_id,rm_id) Values('" + rm_seq + "','" + user_id + "','" + rm_id + "')";
        st.execute(query);

        int result = 0;

        // 방 인원 수 조회
        query = "select count(distinct r.mem_id) " +
                "      from metatourism.room_inmember r " +
                "        left join metatourism.member m    " +
                "      on r.mem_id = m.mem_id  " +
                "        where r.rm_seq = '"+rm_seq+"' and m.mem_state != 0";
        rs = st.executeQuery(query);

        if (rs.next()){
            result = rs.getInt(1);
        }
        // 방 인원수 update
        query = "update room set rm_usercount = "+result+" where rm_seq ='" + rm_seq + "'";
        st.execute(query);

        dbManager.close();
        return result;
    }

    public void userQuitRoom(String rm_seq, String user_id, String rm_id) throws Exception {
        DBManager dbManager = new DBManager();

        dbManager.initConnection();
        ResultSet rs = null;
        Statement st = dbManager.conn.createStatement();
        //  방 멤버에 유저 추가
        String query = "delete from room_inmember where rm_seq = '" + rm_seq + "' and mem_id = '" + user_id + "' and rm_id = '" + rm_id + "'";
        st.execute(query);

        // 방 인원 수 조회
        query = "select count(distinct r.mem_id) " +
                "      from metatourism.room_inmember r " +
                "        left join metatourism.member m    " +
                "      on r.mem_id = m.mem_id  " +
                "        where r.rm_seq = '"+rm_seq+"' and m.mem_state != 0";
        rs = st.executeQuery(query);
        int result = 0;
        if(rs.next()){
            result = rs.getInt(1);
        }


        // 방 인원수 update
        query = "update room set rm_usercount = "+result+" where rm_seq ='" + rm_seq + "'";
        st.execute(query);
        dbManager.close();
    }

    public JSONArray searchRoom(String searchTxt) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        JSONArray arr = new JSONArray();
        int rtn = 0;
        ResultSet rs = null;
        Statement st = dbManager.conn.createStatement();
        searchTxt ="concat('%','"+searchTxt.trim()+"','%')";

        String query = "SELECT a.rm_seq FROM room a left join  member b on  a.mem_seq = b.mem_seq where a.rm_roomnumber like "+searchTxt.replaceAll("\u200B","")+" or b.mem_walletaddr like "+searchTxt.replaceAll("\u200B","")+" or b.mem_id like "+searchTxt.replaceAll("\u200B","")+" ;";
        System.out.println(query);
        rs = st.executeQuery(query);
        while (rs.next()) {
            rtn = rs.getInt(1);
            arr.put(rtn);
        }
        System.out.println(arr);
        dbManager.close();
        return arr;
    }
    public HashMap purchaseItem(String id, String price, String acode, String bcode, String ccode,  String token) throws Exception {
        System.out.println("BusinessQuery.java");
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        Statement st = dbManager.conn.createStatement();
        ResultSet rs = null;
        String rtnVal = "-1";
        String sql2 ="";
        String result = "";
//        String sql = "UPDATE member " +
//                " SET" +
//                " member_point = member_point-'"+price+"' " +
//                " WHERE mem_id = '"+id+"';";
//        rs = st.executeQuery(sql);
//        if (rs.next()) rtnVal = rs.getString(1);
//        if (rs.next()) {//if update member is success?
        sql2 = "insert into item_purchase_history (iph_price , iph_acode ,iph_bcode ,iph_ccode, mem_id, iph_token)" +
                "values('" + price + "','" + acode + "','" +  bcode +"','" +  ccode +"','"+  id +"','" +  token + "');";
        st.execute(sql2);
        result = "1";
//        } else  {
//            responseMsg = "Try again later.";
//        }
        st.close();
        dbManager.close();
        HashMap map = new HashMap<>();
        map.put("token", token);
        map.put("id",id);
        map.put("result", result);
        return map;

    }
    //아이템 조회
    public List<JSONObject> getAllItems(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        List<JSONObject> list = new ArrayList<>();

        String query = "select iph_acode, iph_bcode, iph_ccode, ifnull(memc_bcode, '')as memc_bcode, ifnull(memc_value,'')as memc_value " +
                "from item_purchase_history i " +
                "left join (select mc.mem_seq,mc.memc_bcode,mc.memc_value,m.mem_id from member_character mc left join member m on m.mem_seq = mc.mem_seq where mem_id = '"+id+"') s \n" +
                "on i.iph_bcode = s.memc_bcode " +
                "where i.mem_id = '"+id+"'; ";

        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);

        while (rs.next()) {
            JSONObject json = new JSONObject();
            json.put("iph_acode", rs.getString(1));
            json.put("iph_bcode", rs.getString(2));
            json.put("iph_ccode", rs.getString(3));
            json.put("memc_bcode", rs.getString(4));
            json.put("memc_value", rs.getString(5));

            list.add(json);
        }
        dbManager.close();
        return list;
    }
    public int invenInfo(String id,String bcode, String ccode) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String query = "select count(*)as cnt " +
                "from member_character " +
                "where memc_bcode = '"+bcode+"' and " +
                "mem_seq = (select mc.mem_seq from member_character mc left join member m on m.mem_seq = mc.mem_seq where mem_id = '"+id+"' limit 1) ; ";
        /*"where mem_id = '"+id+"';";*/
        int rtnVal=0;
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);

        if (rs.next()) {
            if (rs.getInt("cnt")>0) rtnVal = 1;
            else rtnVal = 0; ;
        }

        dbManager.close();
        return rtnVal;
    }

    public void ins_memcinfo(String id,String bcode, String ccode) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String query = "insert into member_character (mem_seq,memc_bcode,memc_value) " +
                "values ((select mc.mem_seq from member_character mc left join member m on m.mem_seq = mc.mem_seq where mem_id = '"+id+"' limit 1),'"+bcode+"','"+ccode+"'); ";
        /*"where mem_id = '"+id+"';";*/
        Statement st = dbManager.conn.createStatement();
        st.executeUpdate(query);

        dbManager.close();
    }

    public void upd_memcinfo(String id,String bcode, String ccode) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String query = "update member_character " +
                "set memc_value = '"+ccode+"' " +
                "where memc_bcode = '"+bcode+"' \n" +
                "and mem_seq = (select mem_seq from (select mc.mem_seq from member_character mc left join member m on m.mem_seq = mc.mem_seq where mem_id = '"+id+"' limit 1) s); ";
        Statement st = dbManager.conn.createStatement();
        st.executeUpdate(query);

        dbManager.close();
    }
    public void del_memcinfo(String id,String bcode, String ccode) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String query = "delete from member_character where " +
                "mem_seq =" +
                "(select mem_seq from (select mc.mem_seq from member_character mc left join member m on m.mem_seq = mc.mem_seq where mem_id = '"+id+"' limit 1) s) " +
                "and memc_bcode = '"+bcode+"' ;";
        /*"where mem_id = '"+id+"';";*/
        Statement st = dbManager.conn.createStatement();
        st.executeUpdate(query);

        dbManager.close();
    }
    public List<JSONObject> inven_furniture(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        List<JSONObject> list = new ArrayList<>();

        String query = "select ifnull(rmi_bcode,'')as rmi_bcode , ifnull(rmi_code,'')as rmi_code, ri.mem_seq, m.mem_id from room_item ri \n" +
                "\t left join member m \n" +
                "    on m.mem_seq = ri.mem_seq\n" +
                "    where m.mem_id = '"+id+"' ;";

        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);

        while (rs.next()) {
            JSONObject json = new JSONObject();
            json.put("rmi_bcode", rs.getString(1));
            json.put("rmi_code", rs.getString(2));

            list.add(json);
        }
        dbManager.close();
        return list;
    }
}

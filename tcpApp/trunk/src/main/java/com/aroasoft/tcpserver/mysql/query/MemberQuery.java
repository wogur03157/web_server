package com.aroasoft.tcpserver.mysql.query;

import com.aroasoft.tcpserver.mysql.DBManager;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MemberQuery {
    public HashMap userLogin (String id, String pwd) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        ResultSet rs = null;
        int rtnVal = 1;
        HashMap map = new HashMap();
        String query = "select s.*,ifnull(a.rm_seq,'0') as rm_seq " +
                "from (select count(*) as cnt, m.mem_point,p.memp_position, m.mem_name,m.mem_seq,ifnull(m.mem_walletaddr,'0') as mem_walletaddr " +
                "from member m left join member_position p  " +
                "        on m.mem_seq=p.mem_seq  " +
                "        where mem_id ='"+id+"' and mem_password='"+pwd+"' group by m.mem_seq) s" +
                " left join room a " +
                "on s.mem_seq = a.mem_seq";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            if (rs.getInt("cnt")>0){
                map.put("rtnVal", 0);
                map.put("point",rs.getString("mem_point"));
                map.put("rmSeq",rs.getString("rm_seq"));
                map.put("addr",rs.getString("mem_walletaddr"));
                if(rs.getString("memp_position")==null ||rs.getString("memp_position").equals("")){
                    map.put("pos","");
                }else{
                    map.put("pos",rs.getString("memp_position"));
                }
                map.put("name", rs.getString("mem_name"));
            }

        } else {
            map.put("rtnVal", 1);
        }
        System.out.println(map);
        rs.close();
        st.close();
        dbManager.close();
        return map;
    }
    public void updTime(String id, String time) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String query = "insert into member_position(mem_seq, mem_lasttime) " +
                "select " +
                "mem_seq, '" + time + "'" +
                "from member " +
                "where mem_id = '" + id + "' " +
                "ON DUPLICATE KEY UPDATE " +
                "mem_lasttime='" + time + "';";

        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        st.close();
        dbManager.close();
    }
    public int idCheck(String id, String email) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;

        String query = "select count(*) cnt from member " +
                "where mem_id ='"+id+"' or mem_email ='"+email+"';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            if (rs.getInt("cnt")>0) rtnVal = 1;
            else rtnVal = 0;
        }
        rs.close();
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public int LogoutCheck(String id,String time) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        String query = "insert into member_position(mem_seq, mem_lasttime) " +
                "select " +
                "mem_seq, '" + time + "'" +
                "from member " +
                "where mem_id = '" + id + "' " +
                "ON DUPLICATE KEY UPDATE " +
                "mem_lasttime='" + time + "';";

        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public int AliveCheck(String str,String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        ResultSet rs = null;
        int rtnVal = 1;
        String query = "SELECT  count(*) cnt FROM member_position p left join member m on p.mem_seq=m.mem_seq where ('"+str+"'- mem_lasttime)>=300 and mem_state='1' and mem_id='"+id+"';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while (rs.next()) {
            if (rs.getInt("cnt")>0) rtnVal = 1;
            else rtnVal = 0;
        }
        /* rtnVal.put("id",list);*/
        rs.close();
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public int nameCheck(String name) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;

        String query = "select count(*) cnt from member " +
                "where mem_name ='"+name+"';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            if (rs.getInt("cnt")>0) rtnVal = 1;
            else rtnVal = 0;
        }
        rs.close();
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public HashMap tokenCheck(String id) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        HashMap map=new HashMap();
        String query = "SELECT memt_token from member_token" +
                "where mem_seq=(select mem_seq from member where mem_id='"+id+"' );";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            String token =rs.getString("memt_token");
            if(token==null||token.equals("")){
                map.put("rtnVal",1);

            }
            else{
                map.put("rtnVal",0);
                map.put("token",token);
            }

        }
        rs.close();
        st.close();
        dbManager.close();
        return map;
    }
    public int insertUser(HashMap map) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;

        /*String query = "insert into member(mem_id, mem_password, mem_name, mem_email) " +
                "values('" + map.get("id") + "','" + map.get("pwd") + "',''" +  map.get("name") + "','" +
                map.get("email") + "');'";*/
        String query = "insert into member(mem_id, mem_password, mem_email) " +
                "values('" + map.get("id") + "','" + map.get("pwd") + "','" +  map.get("email") + "');";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 0;
        st.close();
        dbManager.close();

        return rtnVal;
    }
    public int insertToken(String id, String token) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;
        System.out.println("202");

        /*String query = "insert into member(mem_id, mem_password, mem_name, mem_email) " +
                "values('" + map.get("id") + "','" + map.get("pwd") + "',''" +  map.get("name") + "','" +
                map.get("email") + "');'";*/
        String query = "INSERT INTO member_token (mem_seq,memt_token) select mem_seq,'"+token+"' from member where mem_id='"+id+"'";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        System.out.println("203");
        rtnVal = 0;
        st.close();
        dbManager.close();

        return rtnVal;
    }
    public int DelToken(String token) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 1;

        /*String query = "insert into member(mem_id, mem_password, mem_name, mem_email) " +
                "values('" + map.get("id") + "','" + map.get("pwd") + "',''" +  map.get("name") + "','" +
                map.get("email") + "');'";*/
        String query = "DELETE FROM member_token WHERE memt_token='"+token+"' ";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 0;
        st.close();
        dbManager.close();

        return rtnVal;
    }
    public int characterCheck(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "select count(*) cnt from member_character " +
                "where mem_seq = (select mem_seq from member where mem_id='"+id+"');";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            if (rs.getInt("cnt")>0) rtnVal = 1;
            else rtnVal = 0;
        }
        rs.close();
        st.close();
        dbManager.close();
        return rtnVal;
    }
    public int updateUserName(HashMap map) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "update member set mem_name = '"+map.get("name")+"' where mem_id='"+map.get("id")+"';";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 1;
        st.close();
        dbManager.close();

        return rtnVal;
    }

    public int updateMemState(String state,String id) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "update member set mem_state = '"+state+"' where mem_id in('"+id+"');";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 1;
        st.close();
        dbManager.close();

        return rtnVal;
    }
    public int insertCharacter(HashMap map) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "insert into member_character(mem_seq,memc_bcode,memc_value)" +
                "values((select mem_seq from member where mem_id='"+map.get("id")+"'),'"+map.get("code")+"','"+map.get("value")+"');";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 1;
        st.close();
        dbManager.close();

        return rtnVal;
    }

    public String getCharacter(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        JSONObject result = new JSONObject();
        ResultSet rs = null;
        String query = "select memc_bcode,memc_value from member_character where mem_seq = (select mem_seq from member where mem_id='"+id+"')";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while(rs.next()) {
            result.put(rs.getString("memc_bcode"), rs.getInt("memc_value"));
        }
        rs.close();
        st.close();
        dbManager.close();
        return result.toString();
    }

    public String getUserInfo(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        String email="";
        HashMap map = new HashMap();
        ResultSet rs = null;
        String query = "select mem_email from member where mem_id='"+id+"'";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while(rs.next()) {
            email=rs.getString("mem_email");
        }
        rs.close();
        st.close();
        dbManager.close();
        return email;
    }

    public int updateAddress(HashMap map) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "update member set mem_walletaddr = '"+map.get("address")+"' where mem_id='"+map.get("id")+"';";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 1;
        st.close();
        dbManager.close();

        return rtnVal;
    }


    public int updatePwd(String id, String pwd) throws Exception
    {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        int rtnVal = 0;

        String query = "update member set mem_password = '"+pwd+"' where mem_id='"+id+"';";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        rtnVal = 1;
        st.close();
        dbManager.close();

        return rtnVal;
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

}

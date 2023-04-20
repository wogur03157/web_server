package com.aroasoft.tcpserver.mysql.query;

import com.aroasoft.tcpserver.mysql.DBManager;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.HashMap;

public class DBQueryTest {


    public String getAccountSeq(HashMap map) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String rtnVal = "-1";

        String query = "select update_seq('" + map.get("date") + "','" + map.get("type") + "');";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) rtnVal = rs.getString(1);

        dbManager.close();

        return rtnVal;
    }

    public String getChargeSeq() throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String rtnVal = "-1";

        String query = "select LPAD(acc_seq,7,'0') from account_seq where acc_type='charge';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) rtnVal = rs.getString(1);

        dbManager.close();

        return rtnVal;
    }

   /* public HashMap getSiteAccount(String type) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();


        if(type.equals("110") || type.equals("400")) type = "100";
        if(type.equals("210")) type="200";
        if(type.equals("310")) type="300";

        ResultSet rs = null;

        HashMap map = new HashMap();

        String query = "select gsac_seq, id, pw from (select a.gsac_seq, a.gsac_id as id, a.gsac_password as pw, COALESCE(sum(b.gift_amount),0) as total " +
                " from giftsite_account a" +
                " left join gift b on a.gsac_seq = b.gsac_seq" +
                " where a.gsac_type='"+type+"' and a.gsac_used = 1 " +
                " group by a.gsac_id) g where total <= 950000 limit 1;";
        Statement st = conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            map.put("gsac_seq", rs.getString(1));
            map.put("id", rs.getString(2));
            map.put("pw", rs.getString(3));
        }
        return map;
    }

    public int getSendFee() throws SQLException {
        ResultSet rs = null;

        int rtnVal = -1;

        String query = "select cdif_value from codeinfo where cdif_0=400;";
        Statement st = conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) rtnVal = rs.getInt(1);
        return rtnVal;
    }

    public void updateGift(HashMap map) throws SQLException {
        String pin = "";
        if(map.get("pin") != null){
            pin = ",gift_pin = concat(gift_pin,'("+map.get("pin")+")') ";
        }
        Statement st = conn.createStatement();
        String sql = "update gift set gsac_seq="+map.get("gsac_seq")+",gift_amount="+map.get("charge")
                +", gift_exchangefee="+map.get("fee")+",gift_content='"+map.get("desc")+"',gift_status="+map.get("result")+pin
                +" where gift_seq="+map.get("seq");
        st.execute(sql);
    }*/

    public void updateInfo(String id, JSONObject header, JSONObject body) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        Statement st = dbManager.conn.createStatement();
        String sql = "UPDATE coordinate " +
                " SET" +
                " header = '"+header.toString()+"' " +
                ", body = '"+body.toString()+"' " +
                " WHERE id = '"+id+"'";
        System.out.println(sql);
        st.execute(sql);
        st.close();
        dbManager.close();
    }
    public void updatePing(String id, String time) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        Statement st = dbManager.conn.createStatement();
        String sql = "UPDATE coordinate " +
                " SET" +
                " ping = '"+time+"' " +
                " WHERE id = '"+id+"'";
        st.execute(sql);
        st.close();
        dbManager.close();
    }

    public void inscoordinate(String id, JSONObject header, JSONObject body,String time) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        System.out.println("2");
        System.out.println(header);
        System.out.println(body);
        Statement st = dbManager.conn.createStatement();
        String sql = "INSERT INTO coordinate (id,header,body,ping) VALUES  ('"+id+"','"+header.toString()+"','"+body.toString()+"','"+time+"');";
        System.out.println(body);
        st.execute(sql);
        st.close();
        dbManager.close();
    }
    public HashMap getPing(String id) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        ResultSet rs = null;

        HashMap map = new HashMap();

        String query = "select ping from coordinate" +
                "where id="+id+"";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        if (rs.next()) {
            map.put("ping", rs.getString(1));
        }
        dbManager.close();
        st.close();
        return map;
    }
}

package com.aroasoft.tcpserver.mysql.query;

import com.aroasoft.tcpserver.mysql.DBManager;
import org.json.JSONArray;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AliveQuery {
    public void Alivetime(String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String rtnVal = "-1";
        JSONObject json = new JSONObject(str);
        JSONObject header = json.getJSONObject("header");
        JSONObject body = json.getJSONObject("body");

        String id = body.getString("id");
        String time = body.getString("time");

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

    public List getUserList(String str) throws Exception {
        DBManager dbManager = new DBManager();
        dbManager.initConnection();
        JSONObject rtnVal = new JSONObject();
        ResultSet rs = null;
        List list = new ArrayList();
        String query = "SELECT mem_id FROM member_position p left join member m on p.mem_seq=m.mem_seq where ('"+str+"'- mem_lasttime)>=300 and mem_state='1';";
        Statement st = dbManager.conn.createStatement();
        rs = st.executeQuery(query);
        while (rs.next()) {
            list.add(rs.getString("mem_id"));
        }
       /* rtnVal.put("id",list);*/
        rs.close();
        st.close();
        dbManager.close();
        return list;
    }
}
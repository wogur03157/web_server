package com.aroasoft.tcpserver.mysql.query;

import com.aroasoft.tcpserver.mysql.DBManager;
import org.json.JSONObject;

import java.sql.ResultSet;
import java.sql.Statement;

public class LocationQuery {
    public void insPosition (String str) throws Exception {

        DBManager dbManager = new DBManager();
        dbManager.initConnection();

        ResultSet rs = null;

        String rtnVal = "-1";
        JSONObject json=new JSONObject(str);
        JSONObject header=json.getJSONObject("header");
        JSONObject body=json.getJSONObject("body");
        String pos=json.getString("body");
        String id="";

        if(body.has(id)){
            id=body.getString("id");
        }

        String query = "insert into member_position(mem_seq, memp_position) " +
                "select" +
                "mem_seq,+'"+pos+"'+" +
                "from member " +
                "where mem_id ='"+id+"' " +
                "ON DUPLICATE KEY UPDATE " +
                "memp_position ='"+pos+"';";
        Statement st = dbManager.conn.createStatement();
        st.execute(query);
        st.close();
        dbManager.close();
    }
}

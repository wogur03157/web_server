package com.aroasoft.tcpserver;

import com.aroasoft.tcpserver.mysql.query.AliveQuery;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.Date;

public class Logout extends Thread {
    ListenerAlive listener;

    public Logout(ListenerAlive listener) throws Exception {
        this.listener = listener;
    }

    @Override
    public void run() {

        try {
            System.out.println("Logout Thread start");
            listener.logout();
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}

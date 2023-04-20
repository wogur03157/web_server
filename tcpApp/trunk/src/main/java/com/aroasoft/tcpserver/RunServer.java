package com.aroasoft.tcpserver;

import com.aroasoft.core.tcpserver.common.ContainerServer;
import com.aroasoft.tcpserver.mysql.query.AliveQuery;
import io.netty.handler.ssl.SslContext;
import org.apache.commons.cli.*;
import org.apache.http.ssl.SSLContextBuilder;
import org.json.JSONObject;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;

public class RunServer {
    final static int LOGIN_SERVER_PORT = 12000;
    final static int LOCATION_SERVER_PORT = 12001;
    final static int CHATTING_SERVER_PORT = 12002;
    final static int ALIVE_SERVER_PORT = 12003;
    final static int BUSINESS_SERVER_PORT = 12004;

    public static void main(String[] args) {
        //System.out.println( "Start" );
        //ConfigProperties configProperties = ConfigProperties.getInstance();

        //PropertyConfigurator.configure("./log4j.properties");

        Options options = new Options();
        Option input = new Option("t", "type", true, "running type");
        input.setRequired(true);
        options.addOption(input);

        CommandLineParser parser = new DefaultParser();
        HelpFormatter formatter = new HelpFormatter();
        CommandLine cmd = null;

        try {
            cmd = parser.parse(options, args);
        } catch (ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", options);

            System.exit(1);
        }

        String runtype = cmd.getOptionValue("type");

        if (runtype.equals("1")) {
            try {
                ListenerLogin listenerLogin = new ListenerLogin();
                new ContainerServer().start(LOGIN_SERVER_PORT, listenerLogin);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (runtype.equals("2")) {
            try {
                ListenerLocation listenerLocation = new ListenerLocation();
                new ContainerServer().start(LOCATION_SERVER_PORT, listenerLocation);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (runtype.equals("3")) {
            try {
                ListenerChatting listenerChatting = new ListenerChatting();
                new ContainerServer().start(CHATTING_SERVER_PORT, listenerChatting);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (runtype.equals("4")) {
            try {
                ListenerAlive listenerAlive = new ListenerAlive();
                new ContainerServer().start(ALIVE_SERVER_PORT, listenerAlive);
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else if (runtype.equals("5")) {
            try {
                ListenerBusiness listenerBusiness = new ListenerBusiness();
                new ContainerServer().start(BUSINESS_SERVER_PORT, listenerBusiness);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}

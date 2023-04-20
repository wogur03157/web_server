package com.aroasoft.tcpserver;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.Map;

import org.java_websocket.client.WebSocketClient;
import org.java_websocket.drafts.Draft;
import org.java_websocket.handshake.ServerHandshake;
import org.json.JSONObject;

public class GetUserInfo extends WebSocketClient {

    private JSONObject obj = new JSONObject();
    ListenerChatting listenerChatting = new ListenerChatting();

    public GetUserInfo(URI serverUri, Draft draft) {
        super(serverUri, draft);
    }

    public GetUserInfo(URI serverURI) {
        super(serverURI);
    }

    public GetUserInfo(URI serverUri, Map<String, String> httpHeaders) {
        super(serverUri, httpHeaders);
    }

    @Override
    public void onOpen(ServerHandshake handshakedata) {
        System.out.println("opened connection");
        // if you plan to refuse connection based on ip or httpfields overload: onWebsocketHandshakeReceivedAsClient
    }

    @Override
    public void onMessage(String message) {
        System.out.println("message====" + message);
        obj = new JSONObject(message);
        this.close();
        /*  listenerChatting.getTokenList(message);*/
    }

    @Override
    public void onClose(int code, String reason, boolean remote) {
        // The close codes are documented in class org.java_websocket.framing.CloseFrame
        System.out.println(
                "Connection closed by " + (remote ? "remote peer" : "us") + " Code: " + code + " Reason: "
                        + reason);
    }

    @Override
    public void onError(Exception ex) {
        ex.printStackTrace();
        // if the error is fatal then onClose will be called additionally
    }

    public JSONObject getResult() {
        return this.obj;
    }


}

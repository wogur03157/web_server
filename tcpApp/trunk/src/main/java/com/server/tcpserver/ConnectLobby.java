package com.aroasoft.tcpserver;

import java.util.Vector;


public class ConnectLobby {
    //전역 객체변수로 사용하기 위해 static 객체변수로 생성
    static ConnectLobby instance;
    public Vector LobbyUser = new Vector();
    //생성자를 priavte로 만들어 접근을 막는다
    private ConnectLobby(){}

    //getInstance 메소드를 통해 한번만 생성된 객체를 가져온다.
    public static ConnectLobby getInstance(){
        if(instance == null){ //최초 한번만 new 연산자를 통하여 메모리에 할당한다.
            instance = new ConnectLobby();
        }
        return instance;
    }
}

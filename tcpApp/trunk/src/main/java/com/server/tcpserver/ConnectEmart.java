package com.aroasoft.tcpserver;

import java.util.Vector;


public class ConnectEmart {
    //전역 객체변수로 사용하기 위해 static 객체변수로 생성
    static ConnectEmart instance;
    public Vector EmartUser = new Vector();
    //생성자를 priavte로 만들어 접근을 막는다
    private ConnectEmart(){}

    //getInstance 메소드를 통해 한번만 생성된 객체를 가져온다.
    public static ConnectEmart getInstance(){
        if(instance == null){ //최초 한번만 new 연산자를 통하여 메모리에 할당한다.
            instance = new ConnectEmart();
        }
        return instance;
    }
}

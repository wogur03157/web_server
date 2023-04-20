package com.aroasoft.core.tcpserver.dto;

import java.util.HashMap;
import java.util.Map;

public class Response {
    private int error_code; // 성공하면 0, 0보다 크면 error_msg가 표시됨을 의미
    private String error_msg;
    private Map<String,Object> data;

    public Response() {
        data = new HashMap<String,Object>();
    }

    public int getError_code() {
        return error_code;
    }

    public void setError_code(int error_code) {
        this.error_code = error_code;
    }

    public String getError_msg() {
        return error_msg;
    }

    public void setError_msg(String error_msg) {
        this.error_msg = error_msg;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Response(int error_code, String error_msg) {
        this.error_code = error_code;
        this.error_msg = error_msg;
    }
}

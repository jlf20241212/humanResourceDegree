package com.vs;

public class SuccessData {
    private final int code = 200;
    private Object data;
    private String message = "";
    public SuccessData(Object data){
        this.data = data;
    }

    public int getCode() {
        return code;
    }

    public Object getData() {
        return data;
    }

    public void setData(Object data) {
        this.data = data;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }
}

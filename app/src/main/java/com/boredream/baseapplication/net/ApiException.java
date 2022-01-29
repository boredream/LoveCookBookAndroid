package com.boredream.baseapplication.net;

import com.boredream.baseapplication.base.BaseResponse;

public class ApiException extends Exception {

    private BaseResponse<?> body;

    public BaseResponse<?> getBody() {
        return body;
    }

    public void setBody(BaseResponse<?> body) {
        this.body = body;
    }

    public ApiException(BaseResponse<?> body) {
        this.body = body;
    }

    public String getMsg() {
        if(body == null) return null;
        return body.getMsg();
    }
}

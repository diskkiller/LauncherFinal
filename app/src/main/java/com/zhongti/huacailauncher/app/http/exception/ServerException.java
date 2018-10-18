package com.zhongti.huacailauncher.app.http.exception;

/**
 * <p>描述：处理服务器异常</p>
 * Create by shuheming on 2017/11/22
 */
public class ServerException extends RuntimeException {
    private int errCode;
    private String message;

    public ServerException(int errCode, String msg) {
        super(msg);
        this.errCode = errCode;
        this.message = msg;
    }

    public int getErrCode() {
        return errCode;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
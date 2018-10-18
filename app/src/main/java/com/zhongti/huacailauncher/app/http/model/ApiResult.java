package com.zhongti.huacailauncher.app.http.model;

/**
 * 提供统一的服务器返回数据解析的模板 Java Bean
 * <p>
 * Create by ShuHeMing on 2017/11/26
 */
public class ApiResult<T> {
    private int status;
    private String message;
    private T data;

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    @Override
    public String toString() {
        return "ApiResult{" +
                "status='" + status + '\'' +
                ", message='" + message + '\'' +
                ", data=" + data +
                '}';
    }
}

package com.zhongti.huacailauncher.app.http.exception;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;

import retrofit2.HttpException;


/**
 * 错误/异常处理工具
 * <p>
 * Create by ShuHeMing 2017/11/20
 */
public class ExceptionEngine {

    public static ApiException handleException(Throwable t) {
        ApiException ex = new ApiException(t, ERROR.UNKNOWN);
        ex.setMsg("未知错误");
        if (t instanceof HttpException) {
            HttpException httpException = (HttpException) t;
            ex = new ApiException(httpException, httpException.code());
            /*switch (httpException.code()) {
                case BADREQUEST:
                case UNAUTHORIZED:
                case FORBIDDEN:
                case NOT_FOUND:
                case REQUEST_TIMEOUT:
                case GATEWAY_TIMEOUT:
                case INTERNAL_SERVER_ERROR:
                case BAD_GATEWAY:
                case SERVICE_UNAVAILABLE:
                default:
                    ex.msg = "网络错误,Code:"+httpException.code()+" ,err:"+httpException.getmsg();
                    break;
            }*/
            //ex.setMsg(httpException.getMessage());
            ex.setMsg("网络错误");
        } else if (t instanceof ServerException) {
            ServerException resultException = (ServerException) t;
            ex = new ApiException(resultException, resultException.getErrCode());
            ex.setMsg(resultException.getMessage());
        } else if (t instanceof JsonParseException
                || t instanceof JSONException
                || t instanceof JsonSyntaxException
                || t instanceof JsonSerializer
                || t instanceof NotSerializableException
                || t instanceof android.net.ParseException
                || t instanceof JsonIOException
                || t instanceof MalformedJsonException) {
            ex = new ApiException(t, ERROR.PARSE_ERROR);
//            ex.setMsg("解析错误");
            ex.setMsg("服务器数据异常");
        } /*else if (t instanceof ClassCastException) {
            ex = new ApiException(t, ERROR.CAST_ERROR);
            ex.setMsg("类型转换错误");
        } */ else if (t instanceof ConnectException) {
            ex = new ApiException(t, ERROR.NETWORD_ERROR);
            ex.setMsg("网络差,连接失败");
        } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(t, ERROR.SSL_ERROR);
            ex.setMsg("证书验证失败");
        } else if (t instanceof ConnectTimeoutException) {
            ex = new ApiException(t, ERROR.TIMEOUT_ERROR);
            ex.setMsg("网络差,连接超时");
        } else if (t instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(t, ERROR.TIMEOUT_ERROR);
            ex.setMsg("网络差,连接超时");
        } else if (t instanceof UnknownHostException) {
            ex = new ApiException(t, ERROR.UNKNOWNHOST_ERROR);
//            ex.setMsg("无法解析该域名");
            ex.setMsg("服务器开小差了~~");
        }/* else if (t instanceof NullPointerException) {
            ex = new ApiException(t, ERROR.NULLPOINTER_EXCEPTION);
            ex.setMsg("NullPointerException");
        } else if (t instanceof CompositeException) {
            CompositeException exception = (CompositeException) t;
            List<Throwable> exceptions = exception.getExceptions();
            for (Throwable throwable : exceptions) {

            }
        }*/ else {
            ex = new ApiException(t, ERROR.UNKNOWN);
            ex.setMsg("未知错误");
        }

        return ex;
    }

    /**
     * 约定异常
     */
    public static class ERROR {
        /**
         * 未知错误
         */
        public static final int UNKNOWN = 1000;
        /**
         * 解析错误
         */
        public static final int PARSE_ERROR = UNKNOWN + 1;
        /**
         * 网络错误
         */
        public static final int NETWORD_ERROR = PARSE_ERROR + 1;
        /**
         * 协议出错
         */
        public static final int HTTP_ERROR = NETWORD_ERROR + 1;

        /**
         * 证书出错
         */
        public static final int SSL_ERROR = HTTP_ERROR + 1;

        /**
         * 连接超时
         */
        public static final int TIMEOUT_ERROR = SSL_ERROR + 1;

        /**
         * 调用错误
         */
        public static final int INVOKE_ERROR = TIMEOUT_ERROR + 1;
        /**
         * 类转换错误
         */
        public static final int CAST_ERROR = INVOKE_ERROR + 1;
        /**
         * 请求取消
         */
        public static final int REQUEST_CANCEL = CAST_ERROR + 1;
        /**
         * 未知主机错误
         */
        public static final int UNKNOWNHOST_ERROR = REQUEST_CANCEL + 1;

        /**
         * 空指针错误
         */
        public static final int NULLPOINTER_EXCEPTION = UNKNOWNHOST_ERROR + 1;
    }

}

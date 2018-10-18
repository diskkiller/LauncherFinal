package com.zhongti.huacailauncher.app.config.net.normal;

import android.content.Context;
import android.net.ParseException;

import com.google.gson.JsonIOException;
import com.google.gson.JsonParseException;
import com.google.gson.JsonSerializer;
import com.google.gson.JsonSyntaxException;
import com.google.gson.stream.MalformedJsonException;
import com.zhongti.huacailauncher.app.http.exception.ApiException;
import com.zhongti.huacailauncher.app.http.exception.ServerException;

import org.apache.http.conn.ConnectTimeoutException;
import org.json.JSONException;

import java.io.NotSerializableException;
import java.net.ConnectException;
import java.net.UnknownHostException;
import java.util.List;

import io.reactivex.exceptions.CompositeException;
import me.jessyan.rxerrorhandler.handler.listener.ResponseErrorListener;
import retrofit2.HttpException;
import timber.log.Timber;

/**
 * ================================================
 * 展示 {@link ResponseErrorListener} 的用法
 * <p>
 * 用来处理 rxjava 中发生的所有错误,rxjava 中发生的每个错误都会回调此接口
 * rxjava必要要使用{@link me.jessyan.rxerrorhandler.handler.ErrorHandleSubscriber}(默认实现Subscriber的onError方法),此监听才生效
 * <p>
 * Created by shuheming on 2018/1/10 0010.
 * ================================================
 */
@Deprecated
public class ResponseErrorListenerImpl implements ResponseErrorListener {

    @Override
    public void handleResponseError(Context context, Throwable t) {
        Timber.tag("Catch-Error").w(t.getMessage());
        //这里不光是只能打印错误,还可以根据不同的错误作出不同的逻辑处理
        ApiException ex;
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
//            ex.setMsg(httpException.getMessage());
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
                || t instanceof ParseException
                || t instanceof JsonIOException
                || t instanceof MalformedJsonException) {
            ex = new ApiException(t, ERROR.PARSE_ERROR);
            ex.setMsg("解析错误");
        } /*else if (t instanceof ClassCastException) {
            ex = new ApiException(t, ERROR.CAST_ERROR);
            ex.setMsg("类型转换错误");
        } */ else if (t instanceof ConnectException) {
            ex = new ApiException(t, ERROR.NETWORD_ERROR);
            ex.setMsg("连接失败");
        } else if (t instanceof javax.net.ssl.SSLHandshakeException) {
            ex = new ApiException(t, ERROR.SSL_ERROR);
            ex.setMsg("证书验证失败");
        } else if (t instanceof ConnectTimeoutException) {
            ex = new ApiException(t, ERROR.TIMEOUT_ERROR);
            ex.setMsg("连接超时");
        } else if (t instanceof java.net.SocketTimeoutException) {
            ex = new ApiException(t, ERROR.TIMEOUT_ERROR);
            ex.setMsg("连接超时");
        } else if (t instanceof UnknownHostException) {
            ex = new ApiException(t, ERROR.UNKNOWNHOST_ERROR);
            ex.setMsg("无法解析该域名");
        }/* else if (t instanceof NullPointerException) {
            ex = new ApiException(t, ERROR.NULLPOINTER_EXCEPTION);
            ex.setMsg("NullPointerException");
        }*/ else if (t instanceof CompositeException) {
            CompositeException exception = (CompositeException) t;
            List<Throwable> exceptions = exception.getExceptions();
            for (Throwable throwable : exceptions) {
                handleResponseError(context, throwable);
            }
        } else {
            ex = new ApiException(t, ERROR.UNKNOWN);
            ex.setMsg("未知错误");
        }
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

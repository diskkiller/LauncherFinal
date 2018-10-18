package com.zhongti.huacailauncher.app.common;

import org.simple.eventbus.EventBus;

/**
 * ================================================
 * 放置 {@link EventBus} 的 Tag ,便于检索
 * <p>
 * Created by shuheming on 2018/1/5
 * ================================================
 */
public interface EventBusTags {
    /**
     * 强制退出登录的事件
     */
    String EVENT_FORCE_LOGOUT_TIME_UP = "event_force_logout_time_up";
    /**
     * 登录成功
     */
    String EVENT_LOGIN_SUCCESS = "event_login_success";
    /**
     * 退出登录
     */
    String EVENT_LOGIN_OUT = "event_login_out";

    /**
     * 强制退出登录
     */
    String EVENT_FORCE_LOGIN_OUT = "event_force_login_out";
    /**
     * 支付成功
     */
    String EVENT_PAY_SUCCESS = "event_pay_success";
    /**
     * 支付取消
     */
    String EVENT_PAY_CANCEL = "event_pay_cancel";
    /**
     * 兑奖成功
     */
    String EVENT_DJ_SUCCESS = "event_dj_success";
    /**
     * 兑奖成功-for h5
     */
    String EVENT_DJ_SUCCESS_FOR_H5 = "event_dj_success_for_h5";
    /**
     * socket连接成功
     */
    String EVENT_SOCKET_CONNECTED = "event_socket_connected";
    /**
     * 关闭大图预览
     */
    String EVENT_BIG_IMG_CLOSE = "event_big_img_close";
    /**
     * 删除成功
     */
    String EVENT_DEL_LOTTI_SUCCESS = "event_del_lotti_success";
    /**
     * 支付失败
     */
    String EVENT_PAY_FAIL = "event_pay_fail";
    /**
     * 网络连接上了
     */
    String EVENT_NET_CONNECTED = "event_net_connected";
    /**
     * 网络断开了
     */
    String EVENT_NET_DISCONNECTED = "event_net_disconnected";
    /**
     * 刷新h5链接
     */
    String EVENT_REFRESH_H5_LINKS = "event_refresh_h5_links";
    /**
     * 金豆增加/减少 成功
     */
    String EVENT_ADD_LESS_SUCCESS = "event_add_less_success";
    /**
     * 金豆增加/减少 失败
     */
    String EVENT_ADD_LESS_FAIL = "event_add_less_fail";
    /**
     * 取消录票直播成功
     */
    String EVENT_CANCEL_CT_SUCCESS = "event_cancel_ct_success";
    /**
     * 验票直播轮训
     */
    String EVENT_CHECK_TICKET_LIST = "event_check_ticket_list";
    /**
     * 彩票顶部数据刷新
     */
    String EVENT_LOTTERY_TOP_DATA_REFRESH = "event_lottery_top_data_refresh";
    /**
     * 兑奖页面关闭的事件
     */
    String EVENT_ON_DJ_PAGE_CLOSE = "event_on_dj_page_close";
    /**
     * 彩票H5页面关闭了
     */
    String EVENT_ON_LOTTERY_PAGE_CLOSE = "event_on_lottery_page_close";
}

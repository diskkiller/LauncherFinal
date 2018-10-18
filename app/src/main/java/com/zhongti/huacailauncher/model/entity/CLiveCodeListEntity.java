package com.zhongti.huacailauncher.model.entity;

import java.util.List;

/**
 * Create by ShuHeMing on 18/7/10
 */
public class CLiveCodeListEntity {

    /**
     * ticketList : [{"code":"35029236779190017987320983018","money":"10","sort":"绿翡翠","dict":"北京","userId":89,"flag":"1"}]
     * total : 1
     */
    private int isExist; //1 存在 0 不在
    private int total;
    private List<TicketListBean> ticketList;

    public int getIsExist() {
        return isExist;
    }

    public void setIsExist(int isExist) {
        this.isExist = isExist;
    }

    public int getTotal() {
        return total;
    }

    public void setTotal(int total) {
        this.total = total;
    }

    public List<TicketListBean> getTicketList() {
        return ticketList;
    }

    public void setTicketList(List<TicketListBean> ticketList) {
        this.ticketList = ticketList;
    }

    public static class TicketListBean {
        /**
         * code : 35029236779190017987320983018
         * money : 10
         * sort : 绿翡翠
         * dict : 北京
         * userId : 89
         * flag : 1
         */
        private int index;
        private String code;
        private String money;
        private String sort;
        private String dict;
        private int userId;
        private String flag;

        public int getIndex() {
            return index;
        }

        public void setIndex(int index) {
            this.index = index;
        }

        public String getCode() {
            return code;
        }

        public void setCode(String code) {
            this.code = code;
        }

        public String getMoney() {
            return money;
        }

        public void setMoney(String money) {
            this.money = money;
        }

        public String getSort() {
            return sort;
        }

        public void setSort(String sort) {
            this.sort = sort;
        }

        public String getDict() {
            return dict;
        }

        public void setDict(String dict) {
            this.dict = dict;
        }

        public int getUserId() {
            return userId;
        }

        public void setUserId(int userId) {
            this.userId = userId;
        }

        public String getFlag() {
            return flag;
        }

        public void setFlag(String flag) {
            this.flag = flag;
        }
    }
}

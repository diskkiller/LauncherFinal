package com.zhongti.huacailauncher.widget.errcall;


import com.kingja.loadsir.callback.Callback;
import com.zhongti.huacailauncher.R;


/**
 * Description:TODO
 * Create Time:2017/9/4 10:20
 * Author:KingJA
 * Email:kingjavip@gmail.com
 */

public class ErrorCallback extends Callback {
    @Override
    protected int onCreateView() {
        return R.layout.layout_placeholder_error;
    }

    @Override
    public boolean getSuccessVisible() {
        return super.getSuccessVisible();
    }
}
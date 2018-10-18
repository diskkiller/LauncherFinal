package com.zhongti.huacailauncher.widget.errcall;

import android.content.Context;
import android.view.View;

import com.kingja.loadsir.callback.Callback;
import com.zhongti.huacailauncher.R;

/**
 * Create by ShuHeMing on 18/7/3
 */
public class TransCallBack extends Callback {

    @Override
    protected int onCreateView() {
        return R.layout.placeholder_trans;
    }

    @Override
    protected boolean onReloadEvent(Context context, View view) {
        return true;
    }

}

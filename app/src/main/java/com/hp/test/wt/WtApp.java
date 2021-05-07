package com.hp.test.wt;

import android.app.Application;
import android.util.Log;

import com.meituan.android.walle.WalleChannelReader;

/**
 * Created by zhanghuipeng on 5/6/21.
 */
public class WtApp extends Application {
    private static final String TAG = "WtApp";
    @Override
    public void onCreate() {
        super.onCreate();
        initChannel();
    }

    private void initChannel() {
        String channel = WalleChannelReader.getChannel(getApplicationContext(), "");
        Log.d(TAG, "initChannel---"+channel);
        System.out.println("initChannel---"+channel);

    }
}

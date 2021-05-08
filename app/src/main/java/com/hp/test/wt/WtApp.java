package com.hp.test.wt;

import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.multidex.MultiDex;

import com.hotfix.tinker.Log.MyLogImp;
import com.hotfix.tinker.util.SampleApplicationContext;
import com.hotfix.tinker.TinkerManager;
import com.meituan.android.walle.WalleChannelReader;
import com.tencent.tinker.anno.DefaultLifeCycle;
import com.tencent.tinker.entry.DefaultApplicationLike;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareConstants;

/**
 * Created by zhanghuipeng on 5/6/21.
 */
@DefaultLifeCycle(application = "com.hp.test.wt.WalleTinkerApp",
        flags = ShareConstants.TINKER_ENABLE_ALL,
        loaderClass = "com.tencent.tinker.loader.TinkerLoader",
        loadVerifyFlag = false)
//application类名。只能用字符串，这个WalleTinkerApp文件是不存在的，但可以在AndroidManifest.xml的application标签上使用（name）
//loaderClass = "com.tencent.tinker.loader.TinkerLoader",//loaderClassName, 我们这里使用默认即可!（可不写）
public class WtApp extends DefaultApplicationLike {
    private static final String TAG = "WtApp";

    public WtApp(Application application, int tinkerFlags, boolean tinkerLoadVerifyFlag, long applicationStartElapsedTime, long applicationStartMillisTime, Intent tinkerResultIntent) {
        super(application, tinkerFlags, tinkerLoadVerifyFlag, applicationStartElapsedTime, applicationStartMillisTime, tinkerResultIntent);
    }

    @Override
    public void onBaseContextAttached(Context base) {
        super.onBaseContextAttached(base);
        //you must install multiDex whatever tinker is installed!
        MultiDex.install(base);

        SampleApplicationContext.application = getApplication();
        SampleApplicationContext.context = getApplication();
        TinkerManager.setTinkerApplicationLike(this);

        TinkerManager.initFastCrashProtect();
        //should set before tinker is installed
        TinkerManager.setUpgradeRetryEnable(true);

        //optional set logIml, or you can use default debug log
        TinkerInstaller.setLogIml(new MyLogImp());

        //installTinker after load multiDex
        //or you can put com.tencent.tinker.** to main dex
        TinkerManager.installTinker(this);
        Tinker tinker = Tinker.with(getApplication());
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initChannel();
    }

    private void initChannel() {
        String channel = WalleChannelReader.getChannel(getApplication(), "");
        Log.d(TAG, "initChannel---"+channel);
        System.out.println("initChannel---"+channel);

    }
}

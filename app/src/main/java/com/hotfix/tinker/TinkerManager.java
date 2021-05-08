/*
 * Tencent is pleased to support the open source community by making Tinker available.
 *
 * Copyright (C) 2016 THL A29 Limited, a Tencent company. All rights reserved.
 *
 * Licensed under the BSD 3-Clause License (the "License"); you may not use this file except in
 * compliance with the License. You may obtain a copy of the License at
 *
 * https://opensource.org/licenses/BSD-3-Clause
 *
 * Unless required by applicable law or agreed to in writing, software distributed under the License is
 * distributed on an "AS IS" basis, WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND,
 * either express or implied. See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.hotfix.tinker;

import android.annotation.SuppressLint;

import com.hotfix.tinker.crash.SampleUncaughtExceptionHandler;
import com.hotfix.tinker.reporter.SampleLoadReporter;
import com.hotfix.tinker.reporter.SamplePatchListener;
import com.hotfix.tinker.reporter.SamplePatchReporter;
import com.hotfix.tinker.service.SampleResultService;
import com.tencent.tinker.entry.ApplicationLike;
import com.tencent.tinker.lib.patch.AbstractPatch;
import com.tencent.tinker.lib.patch.UpgradePatch;
import com.tencent.tinker.lib.reporter.LoadReporter;
import com.tencent.tinker.lib.reporter.PatchReporter;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.lib.util.TinkerLog;
import com.tencent.tinker.lib.util.UpgradePatchRetry;


/**
 * Created by zhangshaowen on 16/7/3.
 */
public class TinkerManager {
    private static final String TAG = "Tinker.TinkerManager";

    private static ApplicationLike applicationLike;
    private static SampleUncaughtExceptionHandler uncaughtExceptionHandler;
    private static boolean isInstalled = false;
    @SuppressLint("StaticFieldLeak")
    private static SamplePatchListener patchListener;

    public static void setTinkerApplicationLike(ApplicationLike appLike) {
        applicationLike = appLike;
    }

    public static ApplicationLike getTinkerApplicationLike() {
        return applicationLike;
    }

    public static void initFastCrashProtect() {
        if (uncaughtExceptionHandler == null) {
            uncaughtExceptionHandler = new SampleUncaughtExceptionHandler();
            Thread.setDefaultUncaughtExceptionHandler(uncaughtExceptionHandler);
        }
    }

    public static void setUpgradeRetryEnable(boolean enable) {
        UpgradePatchRetry.getInstance(applicationLike.getApplication()).setRetryEnable(enable);
    }


    /**
     * all use default class, simply Tinker install method
     */
    public static void sampleInstallTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        TinkerInstaller.install(appLike);
        isInstalled = true;

    }

    /**
     * you can specify all class you want.
     * sometimes, you can only install tinker in some process you want!
     *
     * @param appLike
     */
    public static void installTinker(ApplicationLike appLike) {
        if (isInstalled) {
            TinkerLog.w(TAG, "install tinker, but has installed, ignore");
            return;
        }
        //or you can just use DefaultLoadReporter
        LoadReporter loadReporter = new SampleLoadReporter(appLike.getApplication());
        //or you can just use DefaultPatchReporter
        PatchReporter patchReporter = new SamplePatchReporter(appLike.getApplication());
        //or you can just use DefaultPatchListener
        patchListener = new SamplePatchListener(appLike.getApplication());
        //you can set your own upgrade patch if you need
        AbstractPatch upgradePatchProcessor = new UpgradePatch();

        TinkerInstaller.install(appLike,
                loadReporter,
                patchReporter,
                patchListener,
                SampleResultService.class,
                upgradePatchProcessor);

        isInstalled = true;
    }


    /**
     * 完成Patch文件的加载
     * 在不需要从新打包安装的情况下将补丁加载进来，点击加载补丁按钮，进行补丁的加载，加载成功后，下次进入app时，bug就会自动被修复了。
     * @param path                  补丁路径
     * @param md5Value              md5
     */
    public static void loadPatch(String path, String md5Value) {
        if (Tinker.isTinkerInstalled()) {
            patchListener.setCurrentMD5(md5Value);
            TinkerInstaller.onReceiveUpgradePatch(applicationLike.getApplication(), path);

        }
    }
}
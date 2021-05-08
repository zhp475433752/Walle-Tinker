package com.hotfix.tinker.util;

import android.os.Environment;
import android.widget.Toast;

import com.hotfix.tinker.bean.PatchInfo;
import com.tencent.tinker.lib.util.TinkerLog;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.Objects;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okio.BufferedSink;
import okio.Okio;
import okio.Sink;

/**
 * Created by zhanghuipeng on 5/8/21.
 * 热修复下载管理器
 */
public class TinkerPatchDownloadManager {

    private static final String TAG = "PatchDownloadManager";
    private static String mPatchFileDir; //patch要保存的文件夹
    private static final PatchInfo patchInfo = new PatchInfo();

    private static boolean isDownloading = false;

    /**
     * 外部调用入口
     * @param context
     * @param md5
     * @param url
     */
    public static void run(String md5, String url) {
        init();
        patchInfo.downloadUrl = url;
        patchInfo.md5 = md5;
        if (!isDownloading) {
            try {
                downloadPatch();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * 初始化
     * @param context
     */
    private static void init() {
        TinkerLog.i(TAG, "HotFix---------TinkerService-----init");
//        mPatchFileDir = SampleApplicationContext.context.getExternalCacheDir().getAbsolutePath().concat("/tpatch/");//todo 正常路径
        mPatchFileDir = Environment.getExternalStorageDirectory().getAbsolutePath();//todo 测试路径
        File patchFileDir = new File(mPatchFileDir);
        try {
            if (!patchFileDir.exists()) {
                patchFileDir.mkdirs(); //文件夹不存在则创建
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 开始下载
     */
    private static void downloadPatch() {
        TinkerLog.i(TAG, "HotFix---------TinkerService-----Patch开始下载");
        Toast.makeText(SampleApplicationContext.context, "开始下载...", Toast.LENGTH_SHORT).show();
        Request request = new Request.Builder().url(patchInfo.downloadUrl).build();
        new OkHttpClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                isDownloading = false;
                TinkerLog.i(TAG,"HotFix---------TinkerService-----onFailure--"+e);
//                Toast.makeText(SampleApplicationContext.context, "下载失败", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                isDownloading = true;
                Sink sink = null;
                BufferedSink bufferedSink = null;
                try {
                    final String fileName = "ceshi.png";// TODO tinkerPatch.apk
                    File dest = new File(mPatchFileDir,   fileName);
                    sink = Okio.sink(dest);
                    bufferedSink = Okio.buffer(sink);
                    bufferedSink.writeAll(Objects.requireNonNull(response.body()).source());
                    bufferedSink.close();
                    TinkerLog.i(TAG,"HotFix---------TinkerService-----Patch下载成功开始安装");
//                    Toast.makeText(SampleApplicationContext.context, "下载成功！", Toast.LENGTH_SHORT).show();
//                    TinkerManager.loadPatch(file.getAbsolutePath(), patchInfo.md5);
                } catch (Exception e) {
                    e.printStackTrace();
                    TinkerLog.i(TAG,"HotFix---------TinkerService-----Patch下载异常--"+e);
//                    Toast.makeText(SampleApplicationContext.context, "下载异常", Toast.LENGTH_SHORT).show();
                } finally {
                    if(bufferedSink != null){
                        bufferedSink.close();
                    }
                }
            }
        });

    }
}

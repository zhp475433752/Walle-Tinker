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

package com.hotfix.tinker.reporter;

import android.content.Context;

import com.tencent.tinker.lib.listener.DefaultPatchListener;

/**
 * Created by zhangshaowen on 16/4/30.
 * optional, you can just use DefaultPatchListener
 * we can check whatever you want whether we actually send a patch request
 * such as we can check rom space or apk channel
 */
public class SamplePatchListener extends DefaultPatchListener {
    private static final String TAG = "Tinker.SamplePatchListener";
    private String currentMD5;// 从服务端接口获取的当前热修复包的md5

    public void setCurrentMD5(String md5Value) {
        this.currentMD5 = md5Value;
    }

    public SamplePatchListener(Context context) {
        super(context);
    }

    /**
     * because we use the defaultCheckPatchReceived method
     * the error code define by myself should after {@code ShareConstants.ERROR_RECOVER_INSERVICE
     *
     * @param path
     * @param newPatch
     * @return
     */
    @Override
    public int patchCheck(String path, String patchMd5) {
        //TODO patch文件md5较验。
        /**
         * 注：出于安全考虑，服务器需要下发md5，加载更新包之前需要检查服务器下发的md5是否和通过File文件代码生成的是否一致
         * 但是不同机器生成的md5可能会不同,因此这里要保证tinker生成的md5和服务器下发的一直，否则会更新失败
         */
//        if (TextUtils.isEmpty(currentMD5) || TextUtils.isEmpty(patchMd5) || !patchMd5.equals(currentMD5)) {
//            return ShareConstants.ERROR_PATCH_DISABLE;
//        }
        return super.patchCheck(path, patchMd5);
    }
}

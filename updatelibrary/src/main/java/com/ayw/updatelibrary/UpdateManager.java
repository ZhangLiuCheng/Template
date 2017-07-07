package com.ayw.updatelibrary;

import android.content.Context;
import android.util.Log;

import java.io.File;

public class UpdateManager {

    public static final String TAG = "UPDATE_TAG";

    private static UpdateManager sInstance = new UpdateManager();

    private IUpdateNetwork updateNetwork;
    private IUpdateListener updateListener;

    private Context context;

    private UpdateManager() {
        Log.v(TAG, "UpdateManager");
    }

    public static UpdateManager init(Context context, IUpdateNetwork updateNetwork) {
        sInstance.context = context;
        sInstance.updateNetwork = updateNetwork;
        return sInstance;
    }

    /**
     * 检查新版本.
     */
    public void check(IUpdateListener updateListener) {
        if (null == updateNetwork) {
            throw new RuntimeException("updateNetwork must not empty, please UpdateManager.init() first!");
        }
        this.updateListener = updateListener;
        requestVersion();
    }

    // 请求服务器最新版本.
    private void requestVersion() {
        downloadApk(null);

        updateNetwork.version(new IUpdateNetwork.VersionListener() {
            @Override
            public void onSuccess(Version version) {
                if (version.getVersionCode() <= UpdateUtil.getVersionCode(context)) {
                    updateListener.lastVersion();
                    return;
                }
                File apkFile = UpdateUtil.getApkFile(context, version);
                if (apkFile.exists()) {
                    // 本地已经存在，安装apk.
                    verifyAndInstall(apkFile, version);
                } else {
                    downloadApk(version);
                }
            }

            @Override
            public void onFailure(Exception ex) {
                updateListener.onFailure(ex);
                Log.v(TAG, "requestVersion  onFailure" + ex);
            }
        });
    }

    // 验证md5并且安装apk
    private void verifyAndInstall(final File apkFile, final Version version) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                String md5 = UpdateUtil.getFileMD5(apkFile);
                if (version.getMd5().equals(md5)) {
                    UpdateUtil.installApk(context, apkFile);
                } else {
                    // md5不一样，删除本地，重新下载
                    apkFile.delete();
                    downloadApk(version);
                }
            }
        }).start();
    }

    private void downloadApk(Version version) {

    }
}

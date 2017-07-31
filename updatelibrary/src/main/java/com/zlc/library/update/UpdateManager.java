package com.zlc.library.update;

import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;

public class UpdateManager {

    public static final String TAG = "UPDATE_TAG";

    private static UpdateManager sInstance = new UpdateManager();
    private final Handler handler = new Handler(Looper.getMainLooper());

    private IUpdateNetwork updateNetwork;
    private IUpdateListener updateListener;

    private Activity activity;

    private UpdateManager() {
        Log.v(TAG, "UpdateManager");
    }

    public static UpdateManager init(Activity activity, IUpdateNetwork updateNetwork) {
        sInstance.activity = activity;
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
        if (null == updateListener) {
        }
        this.updateListener = updateListener;
        requestVersion();
    }

    public void check() {
        check(null);
    }

    // 请求服务器最新版本.
    private void requestVersion() {
        updateNetwork.version(new IUpdateNetwork.VersionListener() {
            @Override
            public void onSuccess(Version version) {
                if (version.getVersionCode() <= UpdateUtil.getVersionCode(activity)) {
                    updateListener.lastVersion();
                    return;
                }
                File apkFile = UpdateUtil.getApkFile(activity, version);
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            infoUpdate(apkFile, version);
                        }
                    });
                } else {
                    // md5不一样，删除本地，重新下载
                    apkFile.delete();
                    downloadApk(version);
                    updateListener.onFailure(new Exception("md5验证不正确！"));
                }
            }
        }).start();
    }

    // 提示更新
    private void infoUpdate(final File apkFile, Version version) {
        updateListener.infoDailog(version.getTitle(), version.getMessage(), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        }, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                UpdateUtil.installApk(activity, apkFile);
            }
        }).show();
    }

    private void downloadApk(Version version) {
        updateNetwork.downloadFile(version.getDownloadUrl(), new IUpdateNetwork.DownloadListener() {
            @Override
            public void onSuccess(File file) {

            }

            @Override
            public void onFailure(Exception ex) {

            }
        });
    }
}

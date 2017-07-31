package com.zlc.library.update;

import android.app.Activity;
import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.widget.Toast;

/**
 * 检查版本更新监听Listener.
 */
public abstract class IUpdateListener {

    private Activity activity;

    public IUpdateListener(Activity activity) {
        this.activity = activity;
    }

    /**
     * 当前版本是最新版本.
     */
    protected void lastVersion() {
        Log.v(UpdateManager.TAG, "当前版本为最新");
    }

    /**
     * 新版本提示框.
     * @param title
     * @param message
     * @param cancel        取消更新.
     * @param install       安装.
     * @return
     */
    protected Dialog infoDailog(String title, String message,
                      OnClickListener cancel, OnClickListener install) {
        AlertDialog.Builder builder = new AlertDialog.Builder(activity);
        builder.setTitle(title)
                .setMessage(message)
                .setNegativeButton("取消", cancel)
                .setPositiveButton("安装", install);
        return builder.create();
    }

    protected void onFailure(Exception ex) {
        Log.v(UpdateManager.TAG, "软件更新失败: " + ex);
    }
}

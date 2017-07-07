package com.ayw.updatelibrary;

import android.app.Dialog;
import android.content.DialogInterface.OnClickListener;

/**
 * 检查版本更新监听Listener.
 */
public interface IUpdateListener {

    /**
     * 当前版本是最新版本.
     */
    void lastVersion();

    /**
     * 新版本提示框.
     * @param title
     * @param message
     * @param cancel        取消更新.
     * @param install       安装.
     * @return
     */
    Dialog infoDailog(String title, String message,
                      OnClickListener cancel, OnClickListener install);

    void onFailure(Exception ex);
}

package com.ayw.template;

import android.app.Application;

import com.ayw.downloadlibary.DownloadManager;
import com.ayw.httplibrary.HttpHelper;
import com.ayw.httplibrary.IHttpParamSign;
import com.ayw.httplibrary.IResultConvert;
import com.ayw.httplibrary.impl.OkHttpProcessor;
import com.ayw.httplibrary.impl.QyHttpParamSign;
import com.ayw.httplibrary.impl.QyResultConvert;

public class AywApplication extends Application {

    public DownloadManager downloadManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化Http请求
        IHttpParamSign paramSign = new QyHttpParamSign("appId", "privateKey", getApplicationContext());
        IResultConvert resultConvert = new QyResultConvert();
        HttpHelper.init(new OkHttpProcessor(getApplicationContext()), paramSign, resultConvert);

        // 初始化DownloadMananger
        downloadManager = DownloadManager.newInstance(this);
    }
}

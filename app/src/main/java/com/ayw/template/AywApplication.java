package com.ayw.template;

import android.app.Application;

import com.ayw.downloadlibary.DownloadManager;
import com.ayw.template.model.http.HttpHelper;
import com.ayw.template.model.http.IHttpParamSign;
import com.ayw.template.model.http.IResultConvert;
import com.ayw.template.model.http.impl.OkHttpProcessor;
import com.ayw.template.model.http.impl.QyHttpParamSign;
import com.ayw.template.model.http.impl.QyResultConvert;

public class AywApplication extends Application {

    public DownloadManager downloadManager;

    @Override
    public void onCreate() {
        super.onCreate();

        // 初始化Http请求
        IHttpParamSign paramSign = new QyHttpParamSign("", "", getApplicationContext());
        IResultConvert resultConvert = new QyResultConvert();
        HttpHelper.init(new OkHttpProcessor(getApplicationContext()), paramSign, resultConvert);

        // 初始化DownloadMananger
        downloadManager = DownloadManager.newInstance(this);
    }
}

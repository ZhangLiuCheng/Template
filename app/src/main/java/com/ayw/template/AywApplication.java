package com.ayw.template;

import android.app.Application;

import com.ayw.template.model.http.HttpHelper;
import com.ayw.template.model.http.IHttpParamSign;
import com.ayw.template.model.http.IResultConvert;
import com.ayw.template.model.http.impl.OkHttpProcessor;
import com.ayw.template.model.http.impl.QyHttpParamSign;
import com.ayw.template.model.http.impl.QyResultConvert;

public class AywApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        IHttpParamSign paramSign = new QyHttpParamSign("", "", getApplicationContext());
        IResultConvert resultConvert = new QyResultConvert();
        HttpHelper.init(new OkHttpProcessor(getApplicationContext()), paramSign, resultConvert);
    }
}

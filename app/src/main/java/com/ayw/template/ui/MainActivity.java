package com.ayw.template.ui;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.ayw.template.R;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * 网络层隔离
     * 实现IHttpProcessor，可以切换网络请求的框架
     * 实现IHttpParamSign，给请求http的参数签名
     * 实现IResutConvert，过滤请求结果.
     */
    public void http(View view) {
        Intent intent = new Intent(this, HttpActivity.class);
        startActivity(intent);
    }

    /**
     * 下载管理器
     * 1.断点续传
     * 2.通过eTag，lastModify等信息，当服务器资源改变时，取消断点，重新下载。
     */
    public void download(View view) {
        Intent intent = new Intent(this, DownloadActivity.class);
        startActivity(intent);
    }
}

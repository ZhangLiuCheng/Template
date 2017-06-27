package com.ayw.template.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ayw.downloadlibary.DownloadManager;
import com.ayw.downloadlibary.IDownloadCallback;
import com.ayw.template.R;
import com.ayw.template.model.http.HttpHelper;
import com.ayw.template.model.http.ResultCallback;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        DownloadManager.init(this);
    }

    public void request(View view) {
        Map<String, String> params = new HashMap<>();
        params.put("key", "value");
        HttpHelper.obtian().post("http://www.baidu.com", params, new ResultCallback<String>() {
            @Override
            public void onSuccess(String s) {
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onSuccess :\r\n" + s);
//                Log.v("TAG", "onSuccess  "  + s);
            }

            @Override
            public void onFailure(Exception ex) {
                Log.v("TAG", "onFailure  "  + ex);
                TextView tv = (TextView) findViewById(R.id.result);
                tv.setText("onFailure :\r\n" + ex.toString());
            }
        });
    }

    public void download(View view) {

//        ExecutorService mExecutorService = Executors.newFixedThreadPool(5);
//        Runnable run = new Runnable() {
//            @Override
//            public void run() {
//                Log.v("TAG", "========> " + System.currentTimeMillis());
//            }
//        };
//        mExecutorService.submit(run);
//        mExecutorService.submit(run);
//        mExecutorService.submit(run);
//        mExecutorService.submit(run);
//        mExecutorService.submit(run);



        DownloadManager.getInstance().downloadUrl("http://desk.fd.zol-img.com.cn/t_s1280x800c5/g5/M00/0B/06/ChMkJ1e8QY6IMCYBAAR5JMl8qw0AAUqwQKqAnIABHk8356.jpg?downfile=1498570780461.jpg", new IDownloadCallback() {

//        DownloadManager.getInstance().downloadUrl("http://192.168.112.75:8080/App/Food.war", new IDownloadCallback() {
            @Override
            public void onStart(String url) {
                Log.v("TAG", "onStart  ");
            }

            @Override
            public void onLoading(String url, long bytesWritten, long totalSize) {
                Log.v("TAG", "onLoading  " + bytesWritten + " =====   " + totalSize);
            }

            @Override
            public void onPause(String url) {

            }

            @Override
            public void onCancel(String url) {

            }

            @Override
            public void onSuccess(String url, File file) {
                Log.v("TAG", "onSuccess  " + file.getAbsolutePath());
            }

            @Override
            public void onFailure(String url, Exception ex) {
                Log.v("TAG", "onFailure  " + ex);
            }
        });
    }
}

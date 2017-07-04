package com.ayw.template.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.ayw.downloadlibary.DownloadCallback;
import com.ayw.downloadlibary.DownloadManager;
import com.ayw.downloadlibary.IFileProvider;
import com.ayw.template.R;

import java.io.File;

public class DownloadActivity extends AppCompatActivity {

//    private String url = "http://desk.fd.zol-img.com.cn/t_s1280x800c5/g5/M00/0B/06/ChMkJ1e8QY6IMCYBAAR5JMl8qw0AAUqwQKqAnIABHk8356.jpg?downfile=1498570780461.jpg";
    private String url = "http://192.168.112.193:8080/App/Food.war";

    private ProgressBar progressBar;
    private TextView state;
    private TextView path;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadManager = DownloadManager.newInstance(this);

        progressBar = (ProgressBar) findViewById(R.id.progress);
        state = (TextView) findViewById(R.id.state);
        path = (TextView) findViewById(R.id.filePath);
    }

    public void download(View view) {
        downloadManager.downloadUrl(url, new MyDownloadCallback());
    }

    public void pause(View view) {
        downloadManager.pauseUrl(url);
    }

    public void cancel(View view) {
        boolean isDownloading = downloadManager.cancelUrl(url);
        if (!isDownloading) {
            resetInfo();
        }
    }

    private void resetInfo() {
        progressBar.setProgress(0);
        path.setText("");
        state.setText("");
    }

    private class MyDownloadCallback extends DownloadCallback {
        @Override
        public void onStart(String url, IFileProvider.Header header) {
            state.setText("开始下载");
        }

        @Override
        public void onLoading(String url, long bytesWritten, long totalSize) {
            state.setText("下载中");
            progressBar.setProgress((int) (bytesWritten * 100 / totalSize));
        }

        @Override
        public void onPause(String url) {
            state.setText("暂停");
        }

        @Override
        public void onCancel(String url) {
            resetInfo();
        }

        @Override
        public void onSuccess(String url, File file) {
            path.setText(file.getAbsolutePath());
            state.setText("下载完成");
        }

        @Override
        public void onFailure(String url, Exception ex) {
            state.setText("下载失败" + ex.getMessage());
        }
    }
}

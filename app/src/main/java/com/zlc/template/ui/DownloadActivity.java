package com.zlc.template.ui;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.zlc.library.download.DownloadCallback;
import com.zlc.library.download.DownloadManager;
import com.zlc.library.download.IFileProvider;
import com.zlc.template.AywApplication;
import com.zlc.template.R;

import java.io.File;

/**
 * 下载管理器
 * 1.断点续传
 * 2.通过eTag，lastModify等信息，当服务器资源改变时，取消断点，重新下载。
 */
public class DownloadActivity extends AppCompatActivity {

//    private String url = "http://desk.fd.zol-img.com.cn/t_s1280x800c5/g5/M00/0B/06/ChMkJ1e8QY6IMCYBAAR5JMl8qw0AAUqwQKqAnIABHk8356.jpg?downfile=1498570780461.jpg";
//    public static String url = "http://192.168.112.193:8080/App/Food.war";
    public static String url = "http://ow655xzyu.bkt.clouddn.com/app-debug.apk";

    private ProgressBar progressBar;
    private TextView state;
    private TextView path;
    private DownloadManager downloadManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        downloadManager = ((AywApplication) getApplication()).downloadManager;

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

    public void clear(View view) {
        downloadManager.clearCache();
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
            state.setText("下载失败:" + ex.toString());
        }
    }
}

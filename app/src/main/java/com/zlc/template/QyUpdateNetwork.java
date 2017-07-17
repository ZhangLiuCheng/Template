package com.zlc.template;

import android.content.Context;

import com.zlc.library.download.DownloadCallback;
import com.zlc.library.download.DownloadManager;
import com.zlc.library.http.HttpHelper;
import com.zlc.library.http.ResultCallback;
import com.zlc.library.update.IUpdateNetwork;
import com.zlc.library.update.Version;

import java.io.File;

public class QyUpdateNetwork implements IUpdateNetwork {

    private DownloadManager downloadManager;

    public QyUpdateNetwork(Context context) {
        downloadManager = DownloadManager.newInstance(context);
    }

    @Override
    public void version(final VersionListener versionListener) {

        HttpHelper.obtian().get("http://www.baidu.com", null, new ResultCallback<Version>() {
            @Override
            public void onSuccess(Version result) {
                versionListener.onSuccess(result);
            }

            @Override
            public void onFailure(Exception ex) {
                versionListener.onFailure(ex);
            }
        });

    }

    @Override
    public void downloadFile(String url, final DownloadListener downloadListener) {
        downloadManager.downloadUrl(url, new DownloadCallback() {
            @Override
            public void onLoading(String url, long bytesWritten, long totalSize) {

            }

            @Override
            public void onSuccess(String url, File file) {
                downloadListener.onSuccess(file);
            }

            @Override
            public void onFailure(String url, Exception ex) {
                downloadListener.onFailure(ex);
            }
        });
    }
}

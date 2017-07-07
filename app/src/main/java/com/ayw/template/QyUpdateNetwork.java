package com.ayw.template;

import android.content.Context;

import com.ayw.downloadlibary.DownloadCallback;
import com.ayw.downloadlibary.DownloadManager;
import com.ayw.httplibrary.HttpHelper;
import com.ayw.httplibrary.ResultCallback;
import com.ayw.updatelibrary.IUpdateNetwork;
import com.ayw.updatelibrary.Version;

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

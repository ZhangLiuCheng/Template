package com.ayw.downloadlibary;

import java.io.File;

public interface IDownloadCallback {

    void onStart(String url);

    void onLoading(String url, long bytesWritten, long totalSize);

    void onPause(String url);

    void onCancel(String url);

    void onSuccess(String url, File file);

    void onFailure(String url, Exception ex);
}

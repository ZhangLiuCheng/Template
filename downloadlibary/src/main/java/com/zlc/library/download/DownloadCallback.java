package com.zlc.library.download;

import java.io.File;

public abstract class DownloadCallback {

    public void onStart(String url, IFileProvider.Header header) {}

    public void onPause(String url) {}

    public void onCancel(String url) {}

    public void onLoading(String url, long bytesWritten, long totalSize) {}

    public abstract void onSuccess(String url, File file);

    public abstract void onFailure(String url, Exception ex);
}

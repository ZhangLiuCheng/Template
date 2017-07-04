package com.ayw.downloadlibary;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.Iterator;
import java.util.List;

final class TransferBack extends DownloadCallback {

    private static final Handler mHandler = new Handler(Looper.getMainLooper());

    private final DownloadCallback callback;
    private List<DownloadFile> mDownloadCallbacks;
    private IFileProvider mFileProvider;

    public TransferBack(DownloadCallback callback, List<DownloadFile> downloadCallbacks,
                        IFileProvider fileProvider) {
        this.callback = callback;
        this.mDownloadCallbacks = downloadCallbacks;
        this.mFileProvider = fileProvider;
    }

    @Override
    public void onStart(final String url, final IFileProvider.Header header) {
        mFileProvider.saveHeaderByUrl(url, header);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onStart(url, header);
            }
        });
    }

    @Override
    public void onLoading(final String url, final long bytesWritten, final long totalSize) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onLoading(url, bytesWritten, totalSize);
            }
        });
    }

    @Override
    public void onPause(final String url) {
        removeDownloadUrl(url);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onPause(url);
            }
        });
    }

    @Override
    public void onCancel(final String url) {
        removeDownloadUrl(url);
        mFileProvider.deleteFileByUrl(url);
        mFileProvider.deleteHeaderByUrl(url);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onCancel(url);
            }
        });
    }

    @Override
    public void onSuccess(final String url, final File file) {
        removeDownloadUrl(url);
        mFileProvider.deleteHeaderByUrl(url);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onSuccess(url, file);
            }
        });
    }

    @Override
    public void onFailure(final String url, final Exception ex) {
        removeDownloadUrl(url);
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                callback.onFailure(url, ex);
            }
        });
    }

    private void removeDownloadUrl(String url) {
        Iterator<DownloadFile> it = mDownloadCallbacks.iterator();
        DownloadFile df;
        while (it.hasNext()) {
            df = it.next();
            if (df.getDownloadUrl().equals(url)) {
                it.remove();
                Log.v("TAG", "remove");
            }
        }
    }
}

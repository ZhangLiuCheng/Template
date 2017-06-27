package com.ayw.downloadlibary;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {

    private static final class DownloadManagerHolder {
        private static final DownloadManager INSTANCE = new DownloadManager();
    }

    private final Handler mHandler = new Handler(Looper.getMainLooper());

    private List<DownloadFile> mDownloadCallbacks = new ArrayList<>();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    private Context context;
    private FileProvider mFileProvider;

    public static void init(Context context) {
        DownloadManager instance = getInstance();
        instance.context = context;
        instance.mFileProvider = new FileProvider(context);
    }

    public static DownloadManager getInstance() {
        return DownloadManagerHolder.INSTANCE;
    }

    public void downloadUrl(String url, IDownloadCallback downCallback) {
        DownloadFile wrdf = getDownloadFile(url);
        // 当前没有在下载中
        if (null == wrdf) {
            DownloadFile downloadFile = new DownloadFile(url,
                    new ProxyCallback(downCallback), mFileProvider.fileByUrl(url));
            mDownloadCallbacks.add(downloadFile);
            mExecutorService.submit(downloadFile);
        } else {
            wrdf.addDownloadCallback(new ProxyCallback(downCallback));
        }
    }

    public void pauseUrl(String url) {
        DownloadFile wrdf = getDownloadFile(url);
        if (null == wrdf) return;
        wrdf.pause();
    }

    public void cancelUrl(String url) {
        DownloadFile wrdf = getDownloadFile(url);
        if (null == wrdf) return;
        wrdf.cancel();
    }

    private DownloadFile getDownloadFile(String url) {
        Iterator<DownloadFile> it = mDownloadCallbacks.iterator();
        DownloadFile df;
        while (it.hasNext()) {
            df = it.next();
            if (df.getDownloadUrl().equals(url)) {
                return df;
            }
        }
        return null;
    }

    private void removeDownloadUrl(String url) {
        Iterator<DownloadFile> it = mDownloadCallbacks.iterator();
        DownloadFile df;
        while (it.hasNext()) {
            df = it.next();
            if (df.getDownloadUrl().equals(url)) {
                it.remove();
            }
        }
    }

    private class ProxyCallback implements IDownloadCallback {

        private final IDownloadCallback callback;

        private ProxyCallback(IDownloadCallback callback) {
            this.callback = callback;
        }

        @Override
        public void onStart(final String url) {
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    callback.onStart(url);
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
    }
}

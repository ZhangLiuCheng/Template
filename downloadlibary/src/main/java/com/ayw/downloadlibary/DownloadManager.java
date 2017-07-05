package com.ayw.downloadlibary;

import android.content.Context;

import java.io.File;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class DownloadManager {

    private List<DownloadFile> mDownloadCallbacks = new ArrayList<>();
    private ExecutorService mExecutorService = Executors.newFixedThreadPool(5);

    private IFileProvider mFileProvider;

    private DownloadManager() {}

    public static DownloadManager newInstance(Context context) {
        DownloadManager instance = new DownloadManager();
        instance.mFileProvider = new DefaultFileProvider(context);
        return instance;
    }

    public static DownloadManager newInstance(IFileProvider fileProvider) {
        DownloadManager instance = new DownloadManager();
        instance.mFileProvider = fileProvider;
        return instance;
    }

    public void downloadUrl(String url, File destFile, DownloadCallback downCallback) {
        if (null == destFile) {
            destFile =  mFileProvider.fetchFileByUrl(url);
        }
        DownloadFile wrdf = getDownloadFile(url);
        // 当前url在下载中
        if (null != wrdf && destFile.getAbsolutePath().equals(wrdf.getDownloadFile().getAbsolutePath())) {
            wrdf.getBridgeCallback().addDownloadCallback(downCallback);
        } else {
            BridgeCallback bridgeCallback = new BridgeCallback(downCallback, mDownloadCallbacks, mFileProvider);
            DownloadFile downloadFile = new DownloadFile(url, bridgeCallback, destFile,
                    mFileProvider.fetchHeaderByUrl(url));
            mDownloadCallbacks.add(downloadFile);
            mExecutorService.submit(downloadFile);
        }
    }

    public void downloadUrl(String url, DownloadCallback downCallback) {
        downloadUrl(url, null, downCallback);
    }

    public void pauseUrl(String url) {
        DownloadFile wrdf = getDownloadFile(url);
        if (null == wrdf) return;
        wrdf.pause();
    }

    /**
     * 取消下载.
     * @param url
     * @return false 当前没有下载中，true当前下载中.
     */
    public boolean cancelUrl(String url) {
        DownloadFile wrdf = getDownloadFile(url);
        if (null == wrdf) {
            // 已经下载完成或者没下载过，试着去删除文件
            mFileProvider.deleteFileByUrl(url);
            mFileProvider.deleteHeaderByUrl(url);
            return false;
        } else {
            wrdf.cancel();
        }
        return true;
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
}

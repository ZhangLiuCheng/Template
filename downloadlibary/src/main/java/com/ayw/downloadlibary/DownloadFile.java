package com.ayw.downloadlibary;

import android.util.Log;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

public class DownloadFile implements Runnable {

    private List<WeakReference<IDownloadCallback>> mDownloadCallbacks = new ArrayList<>();

    private String downloadUrl;

    private File downloadFile;

    private long length;
    private long offset;
    private boolean pause;
    private boolean cancel;

    public DownloadFile(String downloadUrl, IDownloadCallback downloadCallback, File downloadFile) {
        this.downloadUrl = downloadUrl;
        this.mDownloadCallbacks.add(new WeakReference(downloadCallback));
        this.downloadFile = downloadFile;
        offset = downloadFile.length();
    }

    public void addDownloadCallback(IDownloadCallback downloadCallback) {
        mDownloadCallbacks.add(new WeakReference(downloadCallback));
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void pause() {
        pause = true;
    }

    public void cancel() {
        cancel = true;
    }

    @Override
    public void run() {
        InputStream is = null;
        RandomAccessFile fos = null;
        try {
//            if (downloadFile.exists()) {
//                if (downloadFile.length() > 0) {
//                    // 如果已经存在，直接下载完成
//                    invokeDownloadFinish();
//                    return;
//                } else {
//                    downloadFile.delete();
//                }
//            }
            File tempFile = new File(downloadFile.getAbsoluteFile() + ".temp");
            if (tempFile.exists()) {
                offset = tempFile.length();
            } else {
                tempFile.createNewFile();
            }
            offset = 1000;
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Range", "bytes=" + offset + "-");
//            conn.setRequestProperty("If-Range", "W/\"8804894-1498541949000\"");

            conn.setRequestProperty("If-Range", "Tue, 23 Aug 2016 14:00:15 GMT");
//            conn.setRequestProperty("Range", "bytes=" + offset + "-");

            conn.connect();

            int code = conn.getResponseCode();
            if (200 == code) {
                offset = 0;
            }
            is = conn.getInputStream();


            fos = new  RandomAccessFile(tempFile, "rw");
            fos.seek(offset);	// 断点
            length = conn.getContentLength() + offset;
            invokeDownloadBegin(length);					// 开始下载

            byte [] buf  = new byte[1024 * 5];
            do {
                int numread = is.read(buf);
                offset += numread;
                invokeDownloading();
                if (numread <= 0) {
                    tempFile.renameTo(downloadFile);
                    invokeDownloadFinish();			// 下载完成
                    break;
                }
                fos.write(buf, 0, numread);

                if (pause) {
                    invokeDownloadPause();
                } else if (cancel) {
                    invokeDownloadCancel();
                }
            } while (true);
        } catch (Exception e) {
            e.printStackTrace();
            invokeDownloadFail(e);							// 下载失败
        } finally {
            closeStream(fos, is);
        }
    }

    // 回调开始下载
    private void invokeDownloadBegin(final long maxLen) {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onStart(downloadUrl);
            }
        }
    }

    // 回调下载中
    private void invokeDownloading() {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            final IDownloadCallback callback = weakCallback.get();
            if (callback != null) {
                callback.onLoading(downloadUrl, offset, length);
            }
        }
    }

    // 回调暂停中
    private void invokeDownloadPause() {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onPause(downloadUrl);
            } else {
                it.remove();
            }
        }
    }

    // 回调取消中
    private void invokeDownloadCancel() {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onPause(downloadUrl);
            } else {
                it.remove();
            }
        }
    }

    // 回调下载完成
    private void invokeDownloadFinish() {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onSuccess(downloadUrl, downloadFile);
            } else {
                it.remove();
            }
        }
    }

    // 回调下载失败
    private void invokeDownloadFail(final Exception ex) {
        Iterator<WeakReference<IDownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<IDownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onFailure(downloadUrl, ex);
            } else {
                it.remove();
            }
        }
    }

    private void closeStream(Closeable...closable) {
        try {
            for (Closeable c : closable) {
                if (null != c) {
                    c.close();
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

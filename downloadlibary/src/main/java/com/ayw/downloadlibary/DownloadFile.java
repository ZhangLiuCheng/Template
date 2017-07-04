package com.ayw.downloadlibary;

import android.text.TextUtils;

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

public final class DownloadFile implements Runnable {

    private List<WeakReference<DownloadCallback>> mDownloadCallbacks = new ArrayList<>();

    private String downloadUrl;
    private File downloadFile;
    private IFileProvider.Header header;

    private long length;
    private long offset;
    private boolean pause;
    private boolean cancel;

    public DownloadFile(String downloadUrl, DownloadCallback downloadCallback, File downloadFile,
                        IFileProvider.Header header) {
        this.downloadUrl = downloadUrl;
        this.mDownloadCallbacks.add(new WeakReference(downloadCallback));
        this.downloadFile = downloadFile;
        this.header = header;
        offset = downloadFile.length();
    }

    public void addDownloadCallback(DownloadCallback downloadCallback) {
        mDownloadCallbacks.add(new WeakReference(downloadCallback));
    }

    public File getDownloadFile() {
        return downloadFile;
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
            if (downloadFile.exists()) {
                if (downloadFile.length() > 0) {
                    // 如果已经存在，直接下载完成
                    offset = length = downloadFile.length();
                    invokeDownloading();
                    invokeDownloadFinish();
                    return;
                } else {
                    downloadFile.delete();
                }
            }
            File tempFile = new File(downloadFile.getAbsoluteFile() + ".temp");
            if (tempFile.exists()) {
                offset = tempFile.length();
            } else {
                tempFile.createNewFile();
            }
            URL url = new URL(downloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            if (offset > 0 && null != header) {
                conn.setRequestProperty("Range", "bytes=" + offset + "-");
                String ifRange = TextUtils.isEmpty(header.etag) ? header.lastModify : header.etag;
                conn.setRequestProperty("If-Range", ifRange);
            }
            conn.connect();
            int code = conn.getResponseCode();
            if (200 == code) {
                offset = 0;
            }
            is = conn.getInputStream();
            fos = new  RandomAccessFile(tempFile, "rw");
            fos.seek(offset);	// 断点
            length = conn.getContentLength() + offset;

            String etag = conn.getHeaderField("ETag");
            String lastModify = conn.getHeaderField("Last-Modified");
            IFileProvider.Header header = new IFileProvider.Header(etag, lastModify);
            invokeDownloadBegin(header);					// 开始下载

            byte [] buf  = new byte[1024 * 5];
            do {
                int numread = is.read(buf);
                offset += numread;
                if (numread <= 0) {
                    tempFile.renameTo(downloadFile);
                    invokeDownloadFinish();			// 下载完成
                    break;
                }
                fos.write(buf, 0, numread);
                invokeDownloading();

                if (pause) {
                    invokeDownloadPause();
                    break;
                } else if (cancel) {
                    invokeDownloadCancel();
                    break;
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
    private void invokeDownloadBegin(IFileProvider.Header header) {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onStart(downloadUrl, header);
            }
        }
    }

    // 回调下载中
    private void invokeDownloading() {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            final DownloadCallback callback = weakCallback.get();
            if (callback != null) {
                callback.onLoading(downloadUrl, offset, length);
            }
        }
    }

    // 回调暂停中
    private void invokeDownloadPause() {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onPause(downloadUrl);
            }
        }
        mDownloadCallbacks.clear();
    }

    // 回调取消中
    private void invokeDownloadCancel() {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onCancel(downloadUrl);
            }
        }
        mDownloadCallbacks.clear();
    }

    // 回调下载完成
    private void invokeDownloadFinish() {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onSuccess(downloadUrl, downloadFile);
            }
        }
        mDownloadCallbacks.clear();
    }

    // 回调下载失败
    private void invokeDownloadFail(final Exception ex) {
        Iterator<WeakReference<DownloadCallback>> it = mDownloadCallbacks.iterator();
        WeakReference<DownloadCallback> weakCallback;
        while (it.hasNext()) {
            weakCallback = it.next();
            if (weakCallback.get() != null) {
                weakCallback.get().onFailure(downloadUrl, ex);
            }
        }
        mDownloadCallbacks.clear();
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

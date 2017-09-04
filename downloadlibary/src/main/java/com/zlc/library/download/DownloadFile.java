package com.zlc.library.download;

import android.text.TextUtils;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

public final class DownloadFile implements Runnable {

    private String mDownloadUrl;
    private BridgeCallback mBridgeCallback;
    private File mDownloadFile;
    private IFileProvider.Header mHeader;

    private long length;
    private long offset;
    private boolean pause;
    private boolean cancel;

    public DownloadFile(String downloadUrl, BridgeCallback downloadCallback, File downloadFile,
                        IFileProvider.Header header) {
        this.mDownloadUrl = downloadUrl;
        this.mBridgeCallback = downloadCallback;
        this.mDownloadFile = downloadFile;
        this.mHeader = header;
        offset = downloadFile.length();
    }

    public BridgeCallback getBridgeCallback() {
        return mBridgeCallback;
    }

    public File getDownloadFile() {
        return mDownloadFile;
    }

    public String getDownloadUrl() {
        return mDownloadUrl;
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
            if (mHeader != null && mDownloadFile.exists() && mDownloadFile.length() > 0) {
                // 如果已经存在，直接下载完成
                offset = length = mDownloadFile.length();
                invokeDownloading();
                invokeDownloadFinish();
                return;
            } else {
                mDownloadFile.delete();
            }
            File tempFile = new File(mDownloadFile.getAbsoluteFile() + ".temp");
            if (tempFile.exists()) {
                offset = tempFile.length();
            } else {
                tempFile.createNewFile();
            }
            URL url = new URL(mDownloadUrl);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(10000);
            conn.setReadTimeout(10000);
            if (offset > 0 && null != mHeader) {
                conn.setRequestProperty("Range", "bytes=" + offset + "-");
                String ifRange = TextUtils.isEmpty(mHeader.etag) ? mHeader.lastModify : mHeader.etag;
                conn.setRequestProperty("If-Range", ifRange);
            }
            conn.connect();
            int code = conn.getResponseCode();
            if (200 == code) {
                offset = 0;
            }
            length = conn.getContentLength() + offset;
            String etag = conn.getHeaderField("ETag");
            String lastModify = conn.getHeaderField("Last-Modified");
            IFileProvider.Header header = new IFileProvider.Header(etag, lastModify);
            invokeDownloadBegin(header);					// 开始下载

            is = conn.getInputStream();
            fos = new  RandomAccessFile(tempFile, "rw");
            fos.seek(offset);	// 断点
            byte [] buf  = new byte[1024 * 5];
            do {
                int numread = is.read(buf);
                offset += numread;
                if (numread <= 0) {
                    tempFile.renameTo(mDownloadFile);
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
        mBridgeCallback.onStart(mDownloadUrl, header);
    }

    // 回调下载中
    private void invokeDownloading() {
        mBridgeCallback.onLoading(mDownloadUrl, offset, length);
    }

    // 回调暂停中
    private void invokeDownloadPause() {
        mBridgeCallback.onPause(mDownloadUrl);
    }

    // 回调取消中
    private void invokeDownloadCancel() {
        mBridgeCallback.onCancel(mDownloadUrl);
    }

    // 回调下载完成
    private void invokeDownloadFinish() {
        mBridgeCallback.onSuccess(mDownloadUrl, mDownloadFile);
    }

    // 回调下载失败
    private void invokeDownloadFail(final Exception ex) {
        mBridgeCallback.onFailure(mDownloadUrl, ex);
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

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

    private String downloadUrl;
    private BridgeCallback bridgeCallback;
    private File downloadFile;
    private IFileProvider.Header header;

    private long length;
    private long offset;
    private boolean pause;
    private boolean cancel;

    public DownloadFile(String downloadUrl, BridgeCallback downloadCallback, File downloadFile,
                        IFileProvider.Header header) {
        this.downloadUrl = downloadUrl;
        this.bridgeCallback = downloadCallback;
        this.downloadFile = downloadFile;
        this.header = header;
        offset = downloadFile.length();
    }

    public BridgeCallback getBridgeCallback() {
        return bridgeCallback;
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
        bridgeCallback.onStart(downloadUrl, header);
    }

    // 回调下载中
    private void invokeDownloading() {
        bridgeCallback.onLoading(downloadUrl, offset, length);
    }

    // 回调暂停中
    private void invokeDownloadPause() {
        bridgeCallback.onPause(downloadUrl);
    }

    // 回调取消中
    private void invokeDownloadCancel() {
        bridgeCallback.onCancel(downloadUrl);
    }

    // 回调下载完成
    private void invokeDownloadFinish() {
        bridgeCallback.onSuccess(downloadUrl, downloadFile);
    }

    // 回调下载失败
    private void invokeDownloadFail(final Exception ex) {
        bridgeCallback.onFailure(downloadUrl, ex);
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

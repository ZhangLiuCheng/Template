package com.zlc.library.update;

import java.io.File;

/**
 * 请求网络版本和下载文件.
 */
public interface IUpdateNetwork {

    interface VersionListener {
        void onSuccess(Version version);
        void onFailure(Exception ex);
    }

    interface DownloadListener {
        void onSuccess(File file);
        void onFailure(Exception ex);
    }

    /**
     * 请求服务器的版本信息.
     * @param versionListener
     */
    void version(VersionListener versionListener);

    /**
     * 下载apk或者差分包.
     * @param url
     * @param downloadListener
     */
    void downloadFile(String url, DownloadListener downloadListener);
}

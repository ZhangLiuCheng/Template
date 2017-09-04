package com.zlc.library.download;

import android.text.TextUtils;

import java.io.File;

public interface IFileProvider {

    class Header {
        public String etag;
        public String lastModify;

        public Header(String etag, String lastModify) {
            this.etag = etag;
            this.lastModify = lastModify;
        }

        @Override
        public boolean equals(Object obj) {
            if (null == obj) return false;
            if (!(obj instanceof IFileProvider)) return false;
            Header dest = (Header) obj;
            String destIfRange = TextUtils.isEmpty(dest.etag) ? dest.lastModify : dest.etag;
            String sourceIfRange = TextUtils.isEmpty(etag) ? lastModify : etag;
            return sourceIfRange.equals(destIfRange);
        }
    }

    /**
     * 根据url获取到下载的File.
     * @param url
     * @return
     */
    File fetchFileByUrl(String url);

    /**
     * 根据下载的url，删除对应的文件.
     * @param url
     */
    void deleteFileByUrl(String url);

    /**
     * 根据url获取head信息
     * @param url
     * @return
     */
    Header fetchHeaderByUrl(String url);

    /**
     * 保存url的header信息.
     * @param url
     * @param header
     */
    void saveHeaderByUrl(String url, Header header);

    /**
     * 根据url删除header信息.
     * @param url
     */
    void deleteHeaderByUrl(String url);

    /**
     * 清除所有缓存.
     */
    boolean clearCache();
}

package com.zlc.library.update;

import android.text.TextUtils;

public class Version {

    private int versionCode;
    private boolean isForce;    // 是否强制更新
    private String title;
    private String message;
    private String md5;         // 文件md5
    private String apkurl;      // 完成apk的地址
    private String patchUrl;    // 增量更新的地址

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    public boolean isForce() {
        return isForce;
    }

    public void setForce(boolean force) {
        isForce = force;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getMd5() {
        return md5;
    }

    public void setMd5(String md5) {
        this.md5 = md5;
    }

    public String getApkurl() {
        return apkurl;
    }

    public void setApkurl(String apkurl) {
        this.apkurl = apkurl;
    }

    public String getPatchUrl() {
        return patchUrl;
    }

    public void setPatchUrl(String patchUrl) {
        this.patchUrl = patchUrl;
    }

    public String getDownloadUrl() {
        if (!TextUtils.isEmpty(patchUrl)) {
            return patchUrl;
        }
       return apkurl;
    }
}

package com.ayw.downloadlibary;

import android.content.Context;
import android.text.TextUtils;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class FileProvider {

    private Context context;

    public FileProvider(Context context) {
        this.context = context;
    }

    /**
     * 根据下载的url获取到下载的File.
     * @param url
     * @return
     */
    public File fileByUrl(String url) {
        File path = getDownloadPath();

        String fileName;
        try {
            String[] vs = url.split("\\.");
            fileName = stringToMD5(url) + "." + vs[vs.length - 1];
        } catch (Exception e) {
            fileName = null;
        }
        if (TextUtils.isEmpty(fileName)) {
            fileName = url.substring(url.length() - 20, url.length()).replace("/", "");
        }
        return new File(path, fileName);
    }

    /**
     * 根据下载的url，删除对应的文件.
     * @param url
     */
    public void deleteFileByUrl(String url) {
        File file = fileByUrl(url);
        if (file.exists()) {
            file.delete();
        }
    }

    /**
     * 清除下载文件.
     */
    public void clearFiles() {

    }

    private File getDownloadPath() {
        File downloadFile = context.getExternalFilesDir("download");
        if (!downloadFile.exists()) {
            File path = context.getFilesDir();
            downloadFile = new File(path + "download");
            if (!downloadFile.exists()) {
                downloadFile.mkdir();
            }
        }
        return downloadFile;
    }

    public static String stringToMD5(String string) {
        byte[] hash;
        try {
            hash = MessageDigest.getInstance("MD5").digest(string.getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        StringBuilder hex = new StringBuilder(hash.length * 2);
        for (byte b : hash) {
            if ((b & 0xFF) < 0x10)
                hex.append("0");
            hex.append(Integer.toHexString(b & 0xFF));
        }
        return hex.toString();
    }
}

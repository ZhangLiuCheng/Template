package com.ayw.downloadlibary;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;
import android.text.TextUtils;

import java.io.File;
import java.security.MessageDigest;

public class DefaultFileProvider implements IFileProvider {

    private Context context;

    public DefaultFileProvider(Context context) {
        this.context = context;
    }

    @Override
    public File fetchFileByUrl(String url) {
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

    @Override
    public void deleteFileByUrl(String url) {
        File file = fetchFileByUrl(url);
        if (file.exists()) {
            file.delete();
        }
    }

    @Override
    public Header fetchHeaderByUrl(String url) {
        String key = stringToMD5(url);
        SharedPreferences preferences = PreferenceManager.getDefaultSharedPreferences(context);
        String etag = preferences.getString(key + "-etag", null);
        String lastModify = preferences.getString(key+ "-lastModify", null);
        if (TextUtils.isEmpty(etag) && TextUtils.isEmpty(lastModify)) {
            return null;
        }
        return new Header(etag, lastModify);
    }

    @Override
    public void saveHeaderByUrl(String url, Header header) {
        String key = stringToMD5(url);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.putString(key + "-etag", header.etag)
                .putString(key + "-lastModify", header.lastModify)
                .commit();
    }

    @Override
    public void deleteHeaderByUrl(String url) {
        String key = stringToMD5(url);
        SharedPreferences.Editor editor = PreferenceManager.getDefaultSharedPreferences(context).edit();
        editor.remove(key + "-etag")
                .remove(key + "-lastModify")
                .commit();
    }

    @Override
    public void clearCache() {

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

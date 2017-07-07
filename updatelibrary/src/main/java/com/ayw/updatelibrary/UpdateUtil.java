package com.ayw.updatelibrary;

import android.app.ActivityManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Environment;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.List;

public class UpdateUtil {

    /**
     * 根据version获取apk的路径.
     * @param context
     * @param version
     * @return
     */
    public static File getApkFile(Context context, Version version) {
        File updatePath = getUpdatePath(context);
        File apkFile = new File(updatePath, version.getMd5() + ".apk");
        return apkFile;
    }

    /**
     * 根据version获取差分包的路径.
     * @param context
     * @param version
     * @return
     */
    public static File getPatchFile(Context context, Version version) {
        File updatePath = getUpdatePath(context);
        File patchFile = new File(updatePath, version.getMd5() + ".patch");
        return patchFile;
    }

    public static File getUpdatePath(Context context) {
        File downloadFile = context.getExternalFilesDir("update");
        if (!downloadFile.exists()) {
            File path = context.getFilesDir();
            downloadFile = new File(path + "update");
            if (!downloadFile.exists()) {
                downloadFile.mkdir();
            }
        }
        return downloadFile;
    }

    // 合并差分包
    public static File patchApk(Context context) {
        // 已经安装apk的路径
        final String oldFile = context.getPackageResourcePath();
        // 合并后apk的路径
        final String newFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/newpatchNew1.apk";
        // 差分包路径
        final String patchFile = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch1.apk";
        // 合并版本
        BsPatch.patch(oldFile, newFile, patchFile);
        return new File(newFile);
    }

    /**
     * 获取当前版本.
     * @param context
     * @return
     */
    public static int getVersionCode(Context context) {
        try {
            PackageInfo pi = context.getPackageManager().getPackageInfo(context.getPackageName(),0);
            return pi.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * 安装apk.
     * @param context
     * @param apkFile
     */
    public static void installApk(Context context, File apkFile) {

    }

    /**
     * 获取文件的MD5.
     * @param file
     * @return
     */
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            if (null != in) {
                try { in.close();} catch (IOException e) {}
            }
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16);
    }

    /**
     * 判断程序是否在后台运行
     * @param context
     * @return
     */
    public static boolean isRunningBackground(Context context) {
        boolean isInBackground = true;
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        if (Build.VERSION.SDK_INT > Build.VERSION_CODES.KITKAT_WATCH) {
            List<ActivityManager.RunningAppProcessInfo> runningProcesses = am.getRunningAppProcesses();
            for (ActivityManager.RunningAppProcessInfo processInfo : runningProcesses) {
                //前台程序
                if (processInfo.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND) {
                    for (String activeProcess : processInfo.pkgList) {
                        if (activeProcess.equals(context.getPackageName())) {
                            isInBackground = false;
                        }
                    }
                }
            }
        } else {
            List<ActivityManager.RunningTaskInfo> taskInfo = am.getRunningTasks(1);
            ComponentName componentInfo = taskInfo.get(0).topActivity;
            if (componentInfo.getPackageName().equals(context.getPackageName())) {
                isInBackground = false;
            }
        }
        return isInBackground;
    }
}

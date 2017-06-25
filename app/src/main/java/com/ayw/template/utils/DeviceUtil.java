package com.ayw.template.utils;

import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Random;
import java.util.UUID;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.ComponentName;
import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;

public class DeviceUtil {

    public static String getVersionName(Context context) {
        try {
            PackageManager packageManager = context.getPackageManager();
            PackageInfo packInfo = packageManager.getPackageInfo(context.getPackageName(), 0);
            return packInfo.versionName;
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 获取随机字符串，用于接口签名.
     * @return
     */
    public static String appR() {
        String base = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < 10; i++) {
            int number = random.nextInt(base.length());
            sb.append(base.charAt(number));
        }
        return sb.toString();
    }

    public static String deviceAndroidId(Context context) {
        try {
            return Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        } catch (Exception e) {
        }
        return null;
    }

    public static String deviceImei(Context context) {
        try {
            TelephonyManager TelephonyMgr = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
            return TelephonyMgr.getDeviceId();
        } catch (Exception e) {
        }
        return null;
    }

    public static String deviceMac(Context context) {
        try {
            WifiManager wm = (WifiManager) context.getApplicationContext().getSystemService(Context.WIFI_SERVICE);
            return wm.getConnectionInfo().getMacAddress();
        } catch (Exception e) {
        }
        return null;
    }

    /**
     * 品牌.
     * @return
     */
    public static String deviceBrand() {
        return Build.BRAND;
    }

    /**
     * 机型.
     * @return
     */
    public static String deviceModel() {
        return Build.MODEL;
    }

    /**
     * 系统版本.
     * @return
     */
    public static String deviceOs() {
//        return String.valueOf(Build.VERSION.SDK_INT);
        return String.valueOf(Build.VERSION.RELEASE);
    }

    /**
     * 获取设备唯一id.
     * @return
     */
    public static String udid() {
        String upId = getUniquePsuedoID();
        return md5(upId);
    }

    // 获得独一无二的Psuedo ID
    private static String getUniquePsuedoID() {
        String serial = null;
        String m_szDevIDShort = "35" +
                Build.BOARD.length() % 10 + Build.BRAND.length() % 10 +
                Build.CPU_ABI.length() % 10 + Build.DEVICE.length() % 10 +
                Build.DISPLAY.length() % 10 + Build.HOST.length() % 10 +
                Build.ID.length() % 10 + Build.MANUFACTURER.length() % 10 +
                Build.MODEL.length() % 10 + Build.PRODUCT.length() % 10 +
                Build.TAGS.length() % 10 + Build.TYPE.length() % 10 +
                Build.USER.length() % 10;
        try {
            serial = Build.class.getField("SERIAL").get(null).toString();
            return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
        } catch (Exception exception) {
            serial = "serial";
        }
        return new UUID(m_szDevIDShort.hashCode(), serial.hashCode()).toString();
    }

    public static String md5(String string){
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
            md.update(string.getBytes());
            return new BigInteger(1, md.digest()).toString(16);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
        return string;
    }

    /**
     * 是否为今天.
     * @param time
     * @return
     */
    public static boolean isToday(long time) {
        // 昨天
        Calendar yCalendar = Calendar.getInstance();
        yCalendar.add(Calendar.DAY_OF_MONTH, -1);
        yCalendar.set(Calendar.HOUR_OF_DAY, 24);
        yCalendar.set(Calendar.MINUTE, 0);
        yCalendar.set(Calendar.SECOND, 0);
        // 今天
        Calendar tCalendar = Calendar.getInstance();
        tCalendar.add(Calendar.DAY_OF_MONTH, 1);
        tCalendar.set(Calendar.HOUR_OF_DAY, 0);
        tCalendar.set(Calendar.MINUTE, 0);
        tCalendar.set(Calendar.SECOND, 0);
        Calendar c = Calendar.getInstance();
        c.setTime(new Date(time));
        if (c.after(yCalendar) && c.before(tCalendar)) {
            return true;
        }
        return false;
    }

    /**
     * 验证是否为手机号码.
     * @param phone
     * @return
     */
    public static boolean isPhone(String phone) {
        String telRegex = "[1][235874]\\d{9}";
        if (TextUtils.isEmpty(phone)) {
            return false;
        }
        return phone.matches(telRegex);
    }

    public static String formatPrice(long price) {
        return String.format("￥%.2f", price / 100.0f);
    }
    
    /**
     *判断当前应用程序处于前台还是后台
     */
    public static boolean isApplicationBroughtToBackground(final Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> tasks = am.getRunningTasks(1);
        if (!tasks.isEmpty()) {
            ComponentName topActivity = tasks.get(0).topActivity;
            if (!topActivity.getPackageName().equals(context.getPackageName())) {
                return true;
            }
        }
        return false;
    }
}

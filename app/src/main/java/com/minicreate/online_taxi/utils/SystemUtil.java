package com.minicreate.online_taxi.utils;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.Configuration;
import android.os.Build;
import android.provider.Settings;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;

import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.List;

public class SystemUtil {
    public final static String IMSI = "IMSI";
    public final static String IMEI = "IMEI";
    private final static String TAG = "SystemUtil";
    private static long exitTime = 0;

    // 重启系统
    public static void restartSys() {
        try {
            Process exeEcho = Runtime.getRuntime().exec("reboot");
        } catch (IOException e) {
            LogUtil.d("", "execCommand Excute exception: " + e.getMessage());
        }
    }

    // 关闭系统
    public static void shutdownSys() {
        try {
            Process exeEcho = Runtime.getRuntime().exec("reboot -p");
        } catch (IOException e) {
            LogUtil.d("", "execCommand Excute exception: " + e.getMessage());
        }
    }

    /**
     * 关闭输入法
     *
     * @param view
     */
    public static boolean hideSoftInputFromWindow(View view) {
        InputMethodManager imm = (InputMethodManager) view.getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (imm.isActive()) { // 没用，一直是返回true
            imm.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
            return true;
        }
        return false;
    }

    public static void getPhoneSoftwareInfo(Context context) {
        LogUtil.i(TAG, "获取手机 RELEASE: " + android.os.Build.VERSION.RELEASE);
        LogUtil.i(TAG, "获取手机 SDK_INT: " + android.os.Build.VERSION.SDK_INT);
        getAppVesionCode(context);
        getAppVesionName(context);
        getSystemVesionCode(context);
    }

    private static String toMD5(String text) throws NoSuchAlgorithmException {
        //获取摘要器 MessageDigest
        MessageDigest messageDigest = MessageDigest.getInstance("MD5");
        //通过摘要器对字符串的二进制字节数组进行hash计算
        byte[] digest = messageDigest.digest(text.getBytes());

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < digest.length; i++) {
            //循环每个字符 将计算结果转化为正整数;
            int digestInt = digest[i] & 0xff;
            //将10进制转化为较短的16进制
            String hexString = Integer.toHexString(digestInt);
            //转化结果如果是个位数会省略0,因此判断并补0
            if (hexString.length() < 2) {
                sb.append(0);
            }
            //将循环结果添加到缓冲区
            sb.append(hexString);
        }
        //返回整个结果
        return sb.toString();
    }

    public static String getUniqueId(Context context) {
        String androidID = Settings.Secure.getString(context.getContentResolver(), Settings.Secure.ANDROID_ID);
        String id = androidID + Build.SERIAL;
        try {
            return toMD5(id);
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
            return id;
        }
    }
//	public static String getDeviceId(Context context) {
//		  StringBuilder deviceId = new StringBuilder();
//		  // 渠道标志
//		  deviceId.append("a");
//		  try {
//		    //wifi mac地址
//		    WifiManager wifi = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//		    WifiInfo info = wifi.getConnectionInfo();
//		    String wifiMac = info.getMacAddress();
//		    if(!isEmpty(wifiMac)){
//		      deviceId.append("wifi");
//		      deviceId.append(wifiMac);
//		      PALog.e("getDeviceId : ", deviceId.toString());
//		      return deviceId.toString();
//		    }
//		    //IMEI（imei）
//		    TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
//		    String imei = tm.getDeviceId();
//		    if(!isEmpty(imei)){
//		      deviceId.append("imei");
//		      deviceId.append(imei);
//		      PALog.e("getDeviceId : ", deviceId.toString());
//		      return deviceId.toString();
//		    }
//		    //序列号（sn）
//		    String sn = tm.getSimSerialNumber();
//		    if(!isEmpty(sn)){
//		      deviceId.append("sn");
//		      deviceId.append(sn);
//		      PALog.e("getDeviceId : ", deviceId.toString());
//		      return deviceId.toString();
//		    }
//		    //如果上面都没有， 则生成一个id：随机码
//		    String uuid = getUUID(context);
//		    if(!isEmpty(uuid)){
//		      deviceId.append("id");
//		      deviceId.append(uuid);
//		      PALog.e("getDeviceId : ", deviceId.toString());
//		      return deviceId.toString();
//		    }
//		  } catch (Exception e) {
//		    e.printStackTrace();
//		    deviceId.append("id").append(getUUID(context));
//		  }
//		  PALog.e("getDeviceId : ", deviceId.toString());
//		  return deviceId.toString();
//		}
//		/**
//		 * 得到全局唯一UUID
//		 */
//		public static String getUUID(Context context){
//		  SharedPreferences mShare = getSysShare(context, "sysCacheMap");
//		  if(mShare != null){
//		    uuid = mShare.getString("uuid", "");
//		  }
//		  if(isEmpty(uuid)){
//		    uuid = UUID.randomUUID().toString();
//		    saveSysMap(context, "sysCacheMap", "uuid", uuid);
//		  }
//		  PALog.e(tag, "getUUID : " + uuid);
//		return uuid;
//		}


    //屏幕像素密度
    public static float getScreenDensity(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        float density = dm.density;
        LogUtil.i(TAG, "density: " + density);
        LogUtil.i(TAG, "density2: " + context.getResources().getDisplayMetrics().density);
        LogUtil.i(TAG, "densityDpi: " + dm.densityDpi);
        return density;
    }

    //屏幕像素密度
    public static int getScreenDp(Context context) {
        Configuration config = context.getResources().getConfiguration();
        int smallestScreenWidth = config.smallestScreenWidthDp;
        LogUtil.i(TAG, "config: " + config.toString());
        LogUtil.i(TAG, "ScreenDp: " + smallestScreenWidth);
        return smallestScreenWidth;
    }

    public static int getWindowWidth(Context context) {
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenWidth = dm.widthPixels;
        LogUtil.i(TAG, "获取屏幕Width: " + mScreenWidth);
        return mScreenWidth;
    }

    public static int getWindowHeigh(Context context) {
        // 获取屏幕分辨率
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        int mScreenHeigh = dm.heightPixels;
        LogUtil.i(TAG, "获取屏幕Heigh: " + mScreenHeigh);
        return mScreenHeigh;
    }

    /**
     * 获取设备分辨率
     *
     * @return
     */
    public static void getScreenPixels(Context context) {
        WindowManager wm = (WindowManager) (context.getSystemService(Context.WINDOW_SERVICE));
        DisplayMetrics dm = new DisplayMetrics();
        wm.getDefaultDisplay().getMetrics(dm);
        LogUtil.i(TAG, "获取设备分辨率: " + dm.toString());
//		return activity.getWindowManager().getDefaultDisplay();
    }

    /**
     * 获取程序版本号
     *
     * @throws NameNotFoundException
     */
    public static float getAppVesionCode(Context context) {
        try {
            float code = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), Context.MODE_PRIVATE).versionCode;
            LogUtil.i(TAG, "获取AppVesionCode: " + code);
            return code;
        } catch (Exception e) {
            return 0;
        }
    }

    /**
     * 获取程序版本名称
     *
     * @throws NameNotFoundException
     */
    public static String getAppVesionName(Context context) {
        try {
            String name = context.getPackageManager().getPackageInfo(
                    context.getPackageName(), Context.MODE_PRIVATE).versionName;
            LogUtil.i(TAG, "获取AppVesionName: " + name);
            return name;
        } catch (Exception e) {
            return "";
        }
    }

    /**
     * 获取系统版本号
     *
     * @throws NameNotFoundException
     */
    public static String getSystemVesionCode(Context context) {
        // return ((TelephonyManager)
        // context.getSystemService(Context.TELEPHONY_SERVICE)).getDeviceSoftwareVersion();
        LogUtil.i(TAG, "获取SystemVesionCode: " + android.os.Build.VERSION.RELEASE);
        return android.os.Build.VERSION.RELEASE;
    }

    /**
     * 获取设备类型(phone、pad)
     */
    public static String deviceStyle(Context context) {
        TelephonyManager telephony = (TelephonyManager) context
                .getSystemService(Context.TELEPHONY_SERVICE);
        int type = telephony.getPhoneType();
        if (type == TelephonyManager.PHONE_TYPE_NONE) {
            return "phone";
        }

        return "pad";
    }

    /**
     * 根据手机的分辨率从 dp 的单位 转成为 px(像素)
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * 根据手机的分辨率从 px(像素) 的单位 转成为 dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /************** 判断app是否在最前端 *************/
    public static boolean isAppOnForeground(Context context) {
        // 最大运行任务数设置为1
        ActivityManager mActivityManager = ((ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE));
        List<RunningTaskInfo> taskInfos = mActivityManager.getRunningTasks(1);
        if (taskInfos.size() > 0 && TextUtils.equals(context.getPackageName(),
                taskInfos.get(0).topActivity.getPackageName())) {
            return true;
        }
        return false;
    }
}

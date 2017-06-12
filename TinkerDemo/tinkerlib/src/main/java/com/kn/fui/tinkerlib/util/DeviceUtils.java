package com.kn.fui.tinkerlib.util;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.telephony.TelephonyManager;

import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.loader.shareutil.ShareConstants;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

/**
 * Created by MaZhihua on 2017/6/9.
 */

public class DeviceUtils {

    /**
     * 获取版本号
     * @return 当前应用的版本号
     */
    public static String getVersion(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.versionCode + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取应用包名
     * @return 当前应用的包名
     */
    public static String getPackageName(Context context) {
        try {
            PackageManager manager = context.getPackageManager();
            PackageInfo info = manager.getPackageInfo(context.getPackageName(), 0);
            return info.packageName + "";
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 获取用户安装的应用包里面Tinker_id的值
     * @param context
     * @return
     */
    public static String getManifestTinkerId(Context context){
        return ShareTinkerInternals.getManifestTinkerID(context.getApplicationContext());
    }

    /**
     * 获取用户安装的补丁包里面Tinker_id的值
     * @param context
     * @return
     */
    public static String getPatchTinkerId(Context context){
        Tinker tinker = Tinker.with(context.getApplicationContext());
        String tinkerId = tinker.getTinkerLoadResultIfPresent().getPackageConfigByName(ShareConstants.TINKER_ID);
        return tinkerId;
    }

    /**
     * 获取补丁包的版本
     * @param context
     * @return
     */
    public static String getPatchVersion(Context context){
        Tinker tinker = Tinker.with(context.getApplicationContext());
        return tinker.getTinkerLoadResultIfPresent().getPackageConfigByName("patchVersion");
    }

    /**
     * 获取当前设备的IMEI
     * @param context
     * @return
     */
    public static String getIMEI(Context context){
        TelephonyManager tm = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        return tm.getDeviceId();
    }

    /**
     * 获取当前设备的Mac address
     * @param context
     * @return
     */
    public static String getMacAddress(Context context){
        WifiManager wifi = (WifiManager) context.getSystemService (Context.WIFI_SERVICE);
        WifiInfo info = wifi.getConnectionInfo();
        return info.getMacAddress();
    }

    /**
     * 获取当前设备的联网方式
     * @param context
     * @return
     */
    //TODO 这块的网络类型需要跟服务端定
    public static String getInternetType(Context context){
        ConnectivityManager mConnectivity = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        //检查网络连接
        NetworkInfo info = mConnectivity.getActiveNetworkInfo();
        if(info != null && info.isConnected()){
            if(info.getType() == 1){ //wifi
                return "wifi";
            }else if(info.getType() == 9){ //以太网，有线
                return "ethernet";
            }
        }
        return "other";
    }
}

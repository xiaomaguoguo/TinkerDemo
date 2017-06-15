package com.kn.fui.tinkerlib.util;

import android.content.Context;
import android.util.Log;

import com.baofengtv.middleware.tv.BFTVCommonManager;
import com.baofengtv.middleware.tv.BFTVFactoryManager;

import java.util.HashMap;

/**
 * Created by MaZhihua on 2017/6/9.
 * 通用参数
 */
public class GlobalParams {

    public static final String DEFAULT_APP_TOKEN = "282340ce12c5e10fa84171660a2054f8";




    /***********请求基础参数**********/

    //Apptoken app授权token,有API开发方提供
    public static final String APP_TOKEN = "apptoken";

    //Appversion 当前应用包的版本号
    public static final String APP_VERSION = "appversion";

    //来自的平台 请求来源..可选值为: mobile,tv
    public static final String FROM = "from";

    //设备mac地址
    public static final String MAC = "mac";

    //设备mac地址 联网方式..可选值为: wifi,gprs
    public static final String NETWORKTYPE= "networktype";

    /***********请求基础参数**********/







    /***********电视公用参数**********/

    //平台信息
    public static final String PLATFORM = "platform";

    //系统版本信息
    public static final String SYS_VERSION = "sys_version";

    //软件id 8位纯数字
    public static final String UUID = "uuid";

    //电视唯一号
    public static final String SOFTID = "softid";

    /***********电视公用参数**********/







    /***********手机公用参数**********/
    //手机imei号
    public static final String IMEI = "imei";
    /***********手机公用参数**********/






    /***********检查是否有升级补丁的请求参数**********/

    //当前应用包的名称
    public static final String PACKAGENAME = "packName";

    //当前应用包的版本号
    public static final String VERSIONCODE = "versionCode";

    //当前应用包的版本名称
//    public static final String VERSIONNAME = "versionName";

    //当前应用包的tinkerId
    public static final String TINKERID = "tinkerId";

    //当前应用包的patchVersion
    public static final String PATCHVERSION = "patchVersion";

    /***********检查是否有升级补丁的请求参数**********/








    /***********客户端上报补丁 下载/安装数据 相关参数**********/

    //下载是否成功..成功为0.失败1
    public static final String DOWNLOADRESULT = "downloadRs";

    //下载失败原因.当下载成功时.此值为空
    public static final String  DOWNLOADFAILMSG= "downloadFailMsg";

    //安装是否成功..成功为0.失败1
    public static final String  INSTALLRESULT= "installRs";

    //安装失败原因.当安装成功时.此值为空
    public static final String  INSTALLFAILMSG= "installFailMsg";

    /***********客户端上报补丁 下载/安装数据 相关参数**********/






    //补丁下载反馈
    public static final int FEEDBACK_TYPE_DOWNLOAD = 1;

    //补丁安装反馈
    public static final int FEEDBACK_TYPE_INSTALL = 2;


    /**
     * 获取补丁包检测基础字段
     * @param context
     * @return
     */
    public static HashMap<String,String> getPatchCheckParams(Context context){
        HashMap<String,String> check = new HashMap<>();
        check.put(PACKAGENAME,DeviceUtils.getPackageName(context));
        check.put(VERSIONCODE,DeviceUtils.getVersionCode(context));
//        check.put(VERSIONNAME,DeviceUtils.getVersionName(context));
        check.put(TINKERID,DeviceUtils.getManifestTinkerId(context));
        check.put(PATCHVERSION,DeviceUtils.getPatchVersion(context));
        return check;
    }

    /**
     * patch补丁下载反馈基础字段
     * @param context
     * @param type {@link #FEEDBACK_TYPE_DOWNLOAD} :下载相关，{@link #FEEDBACK_TYPE_INSTALL}：安装相关
     * @param isDownloadSuccess 下载是否成功
     * @param downloadFailedReason 下载失败原因
     * @param isInstallSuccess 安装是否成功
     * @param installFailedReason 安装失败原因
     * @return Feedback接口参数
     * 备注 ：服务端要求这个地方尽量使用一个接口，所以参数会有点多
     */
    public static HashMap<String,String> getPatchFeedback(Context context,int type,boolean isDownloadSuccess,String downloadFailedReason,boolean isInstallSuccess,String installFailedReason){
        HashMap<String,String> feedback = new HashMap<>();
        feedback.putAll(getCommonParams(context));
        feedback.put(PACKAGENAME,DeviceUtils.getPackageName(context));
        feedback.put(VERSIONCODE,DeviceUtils.getVersionCode(context));
        switch (type){

            case FEEDBACK_TYPE_DOWNLOAD:
                feedback.put(DOWNLOADRESULT,isDownloadSuccess ? "1" : "0");
                feedback.put(DOWNLOADFAILMSG,downloadFailedReason);
                break;

            case FEEDBACK_TYPE_INSTALL:
                feedback.put(INSTALLRESULT,isInstallSuccess ? "1" : "0");
                feedback.put(INSTALLFAILMSG,installFailedReason);
                break;

        }

        return feedback;
    }

    /**
     * 获取接口请求通用参数
     * @param context
     * @return
     */
    public static HashMap<String,String> getCommonParams(Context context){
        HashMap<String,String> common = new HashMap<>();
        common.put(APP_TOKEN,DEFAULT_APP_TOKEN);
        common.put(APP_VERSION, DeviceUtils.getVersionCode(context));
        common.put(MAC,DeviceUtils.getMacAddress(context));
        common.put(NETWORKTYPE,DeviceUtils.getInternetType(context));
        common.putAll(TinkerManager.isTv ? getTVCommonParams(context) : getPhoneCommonParams(context));
        return common;
    }

    /**
     * 获取TV通用参数
     * @param context
     * @return
     */
    public static HashMap<String,String> getTVCommonParams(Context context){
        HashMap<String,String> common = new HashMap<>();
        common.put(FROM,"bftv_android");
        try{
            common.put(PLATFORM, BFTVCommonManager.getInstance(context).getPlatform());
            common.put(SYS_VERSION,BFTVCommonManager.getInstance(context).getVersion() );
            common.put(SOFTID,BFTVCommonManager.getInstance(context).getSoftwareID());
            common.put(UUID, BFTVFactoryManager.getInstance(context).getSerialNumber());
        }catch (Exception e){
            e.printStackTrace();
            Log.i("GlobalParams","获取TV参数异常");
        }
        return common;
    }

    /**
     * 获取手机通用参数
     * @param context
     * @return
     */
    public static HashMap<String,String> getPhoneCommonParams(Context context){
        HashMap<String,String> common = new HashMap<>();
        common.put(IMEI, DeviceUtils.getIMEI(context));
        common.put(FROM,"bftvm_android");
        return common;
    }

}

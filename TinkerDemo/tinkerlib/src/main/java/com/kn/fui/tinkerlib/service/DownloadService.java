package com.kn.fui.tinkerlib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Environment;
import android.os.ResultReceiver;
import android.text.TextUtils;
import android.util.Log;

import com.kn.fui.tinkerlib.util.MD5Utils;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by KNothing on 2017/6/2.
 * 下载服务类
 */
public class DownloadService extends IntentService implements IIntentType {

    public static final String TAG = DownloadService.class.getSimpleName();

    public DownloadService(){
        super("DownloadService");
    }

    public DownloadService(String name) {
        super(name);
    }

    public static void startDownloadPatchService(Context mContext, String patchInfoUrl){
        Intent download = new Intent(mContext,DownloadService.class);
        download.putExtra(INTENT_KEY_TYPE,GET_PATCH_INFO);
        download.putExtra(PATCH_INFO_URL,patchInfoUrl);
        mContext.startService(download);
    }

    @Override
    protected void onHandleIntent(Intent intent) {

        int type = intent.getIntExtra(INTENT_KEY_TYPE,-1);
        if(type == -1){
            return ;
        }

        switch (type){

            case GET_PATCH_INFO: // 获取patch相关信息
                JSONObject jsonObject = getJSONObject(intent.getStringExtra(PATCH_INFO_URL));
                if(jsonObject == null){
                    return ;
                }
                try {
                    Log.d(TAG,"服务端返回的json  = " + jsonObject.toString());
                    String patchTinkerId = jsonObject.getString("tinker_id");
                    String patchDownloadUrl = jsonObject.getString("patch_download_url");
                    String patchMd5 = jsonObject.getString("patchMd5");
                    String manifestTinkerId = ShareTinkerInternals.getManifestTinkerID(getApplicationContext());

                    // 基准包与patch版本不匹配，则不需要下载
                    if(!TextUtils.isEmpty(patchTinkerId) && !TextUtils.isEmpty(manifestTinkerId) && TextUtils.equals(manifestTinkerId,patchTinkerId)){
                        downloadAndSave2Sdcard(patchDownloadUrl,patchMd5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }

    public JSONObject getJSONObject(String urlToDownload) {

        if(TextUtils.isEmpty(urlToDownload)){
            return null;
        }

        InputStream input = null;
        ByteArrayOutputStream baos = null;

        try {
            input = getInputStream(urlToDownload);
            if (input != null) { // 请求正常
                input = new BufferedInputStream(input);
                baos = new ByteArrayOutputStream();
                byte[] buffer = new byte[1024];
                int len = 0;
                while ((len = input.read(buffer)) != -1) {
                    baos.write(buffer, 0, len);
                }
                String json = baos.toString();
                JSONObject jsonObject = new JSONObject(json);
                return jsonObject;
            }
        } catch ( IOException|JSONException e ) {
            e.printStackTrace();
        }finally {
            if(baos != null && input != null){
                try {
                    baos.close();
                    input.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

    public static InputStream getInputStream(String urlToDownload) {
        try {
            URL url = new URL(urlToDownload);
            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setConnectTimeout(10000);
            connection.setReadTimeout(10000);
            connection.setDoOutput(true);             //允许向服务器输出数据
            connection.setDoInput(true);              //允许接收服务器数据
            connection.setRequestMethod("POST");
            connection.setUseCaches(false);           // Post 请求不能使用缓存
            connection.connect();
            if(connection.getResponseCode() == 200 ){
                return connection.getInputStream();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }


    //TODO 以下功能需要测试
    private void downloadAndSave2Sdcard(String downloadUrl,String patchServerMd5){
        // /storage/emulated/0/Android/data/packageName/cache
        String externamCache =  getApplicationContext().getExternalCacheDir().getAbsolutePath();
        String filePath = externamCache.concat("/").concat("patch.abc");
        File downloadFile = new File(filePath);

        try {
            OutputStream output = new FileOutputStream(downloadFile);
            byte data[] = new byte[1024];
            int count;
            InputStream input = getInputStream(downloadUrl);
            if(input != null){
                while ((count = input.read(data)) != -1) {
                    output.write(data, 0, count);
                }
                output.flush();
                output.close();
                input.close();
            }
        } catch (IOException e) {
            e.printStackTrace();
        }


        boolean isSuccess = verifyDownloadFileCorrect(patchServerMd5,downloadFile);
        Log.d(TAG,isSuccess ? "下载并保存成功,开始打补丁..." : "下载或者校验失败...");
        if(isSuccess){ // 下载成功
            TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),filePath);
        }else{// 如果下载失败或者校验失败了，则删除垃圾文件
            SharePatchFileUtil.safeDeleteFile(downloadFile);
        }

    }

    /**
     * 校验下载文件完整性,如果返回false,则需要重新下载
     * @param urlMd5
     * @param downloadFile
     * @return
     */
    private boolean verifyDownloadFileCorrect(String urlMd5,File downloadFile) {
        String downloadMd5 = MD5Utils.getFileMD5(downloadFile);
        Log.d(TAG,"返回的urlMd5 = ".concat(urlMd5).concat(" ;自己拿到的urlMd5 =  ").concat(downloadMd5));
        return !TextUtils.isEmpty(urlMd5) && !TextUtils.isEmpty(downloadMd5) && TextUtils.equals(urlMd5,downloadMd5);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"下载任务完成,服务自动关闭...");
    }

}

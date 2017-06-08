package com.kn.fui.tinkerlib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.kn.fui.tinkerlib.util.IOUtils;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;

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

    /**
     * 开始下载补丁
     * @param mContext
     * @param patchInfoUrl
     */
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
                HashMap<String,String> params = new HashMap<>();
                JSONObject jsonObject = IOUtils.getJSONObject(params,intent.getStringExtra(PATCH_INFO_URL));
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
                    if(IOUtils.checkTinkerIdIsMatch(manifestTinkerId,patchTinkerId)){
                        IOUtils.downloadAndSave2Sdcard(getApplicationContext(),params,patchDownloadUrl,patchMd5);
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                break;

        }

    }


    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG,"下载任务完成,服务自动关闭...");
    }

}

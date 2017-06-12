package com.kn.fui.tinkerlib.service;

import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.util.Log;

import com.kn.fui.tinkerlib.util.GlobalParams;
import com.kn.fui.tinkerlib.util.IOUtils;

import org.json.JSONObject;

import java.util.HashMap;

/**
 * Created by KNothing on 2017/6/2.
 * 检测补丁服务类
 */
public class CheckPatchService extends IntentService implements IIntentType {

    public static final String TAG = CheckPatchService.class.getSimpleName();

    public CheckPatchService(){
        super("CheckPatchService");
    }

    public CheckPatchService(String name) {
        super(name);
    }

    /**
     * 开始下载补丁
     * @param mContext
     */
    public static void startCheckPatchService(Context mContext,boolean isTv){
        Intent download = new Intent(mContext,CheckPatchService.class);
        download.putExtra(INTENT_KEY_TYPE,GET_PATCH_INFO);
        download.putExtra(INTENT_IS_TV,isTv);
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
                params.putAll(GlobalParams.getCommonParams(getApplicationContext()));
                params.putAll(GlobalParams.getPatchCheckParams(getApplicationContext()));
                JSONObject jsonObject = IOUtils.getJSONObject(params, GlobalParams.BASE_URL.concat(GlobalParams.PATCH_CHECK));
                if(jsonObject == null){
                    return ;
                }

                if(TextUtils.equals("0",jsonObject.optString("error_code"))){ // 说明正常
                    Log.d(TAG,"服务端返回的json  = " + jsonObject.toString());
                    JSONObject dataObject = jsonObject.optJSONObject("data");
                    if(dataObject != null){
                        String downloadUrl = dataObject.optString("downloadUrl");
                        String patchMd5 = dataObject.optString("patchMd5");
                        if(!TextUtils.isEmpty(downloadUrl) && !TextUtils.isEmpty(patchMd5)){
                            IOUtils.downloadAndSave2Sdcard(getApplicationContext(),params,downloadUrl,patchMd5); // 下载
                        }
                    }
                }else{
                    Log.i(TAG,jsonObject.optString("msg"));
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

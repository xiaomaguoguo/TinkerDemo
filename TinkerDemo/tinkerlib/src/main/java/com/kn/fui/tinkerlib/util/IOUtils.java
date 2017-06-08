/*
 * Copyright 2014 Google Inc. All rights reserved.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.kn.fui.tinkerlib.util;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.SharePatchFileUtil;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * IO工具类
 */
public class IOUtils {

    public static final String TAG = IOUtils.class.getSimpleName();

    /**
     * 从网络获取流
     * @param urlToDownload
     * @return
     */
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

    /**
     * 将数据写入文件
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeToFile(String data, File file) throws IOException {
        if(data == null){
            return ;
        }
        writeToFile(data.getBytes("UTF-8"), file);
    }

    /**
     * 将字节数组写入文件
     * @param data
     * @param file
     * @throws IOException
     */
    public static void writeToFile(byte[] data, File file) throws IOException {
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(file);
            os.write(data);
            os.flush();
            // Perform an fsync on the FileOutputStream.
            os.getFD().sync();
        } finally {
            if (os != null) {
                os.close();
            }
        }
    }

    /**
     * 将字符串写出到输出流
     * @param content
     * @param os
     * @throws IOException
     */
    public static void writeToStream(String content, OutputStream os) throws IOException {
        BufferedWriter writer = null;
        try {
            writer = new BufferedWriter(new OutputStreamWriter(os, "UTF-8"));
            writer.write(content);
        } finally {
            if (writer != null) {
                writer.close();
            }
        }
    }

    /**
     * 将文件内容读入到String
     * @param file
     * @return
     * @throws IOException
     */
    public static String readFileAsString(File file) throws IOException {
        return readAsString(new FileInputStream(file));
    }

    /**
     * 将输入流转换为String
     * @param is
     * @return
     * @throws IOException
     */
    public static String readAsString(InputStream is) throws IOException {

        if(is == null){
            return null;
        }

        BufferedReader reader = null;
        StringBuilder sb = new StringBuilder();
        try {
            String line;
            reader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
            while ((line = reader.readLine()) != null) {
                sb.append(line);
            }
        } finally {
            if (reader != null) {
                reader.close();
            }
        }
        return sb.toString();
    }

    /**
     * 从url获取返回值，并将返回值转换为JSONObject
     * @param urlToDownload
     * @return
     */
    public static JSONObject getJSONObject(String urlToDownload) {

        if(TextUtils.isEmpty(urlToDownload)){
            return null;
        }

        InputStream input = getInputStream(urlToDownload);
        if(input == null){
            return null;
        }

        try{

            String json = readAsString(input);
            if(TextUtils.isEmpty(json)){
                return null;
            }

            JSONObject jsonObject = new JSONObject(json);
            return jsonObject;
        }catch (IOException | JSONException e){
            e.printStackTrace();
        }
        return null;


        /*InputStream input = null;
        ByteArrayOutputStream baos = null;

        try {
            input = getInputStream(urlToDownload);
            readAsString()
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
        }*/
    }

    //TODO 以下功能需要测试
    public static void downloadAndSave2Sdcard(Context context,String downloadUrl,String patchServerMd5){
        // /storage/emulated/0/Android/data/packageName/cache
        String externamCache =  context.getExternalCacheDir().getAbsolutePath();
        String filePath = externamCache.concat("/").concat("patch.abc");
        File downloadFile = new File(filePath);
        try {
            InputStream input = getInputStream(downloadUrl);
            writeToFile(readAsString(input),downloadFile);
        } catch (IOException e) {
            e.printStackTrace();
        }

//        try {
//            OutputStream output = new FileOutputStream(downloadFile);
//            byte data[] = new byte[1024];
//            int count;
//            InputStream input = getInputStream(downloadUrl);
//            if(input != null){
//                while ((count = input.read(data)) != -1) {
//                    output.write(data, 0, count);
//                }
//                output.flush();
//                output.close();
//                input.close();
//            }
//        } catch (IOException e) {
//            e.printStackTrace();
//        }


        boolean isSuccess = verifyDownloadFileCorrect(patchServerMd5,downloadFile);
        Log.d(TAG,isSuccess ? "下载并保存成功,开始打补丁..." : "下载或者校验失败...");
        if(isSuccess){ // 下载成功
            TinkerInstaller.onReceiveUpgradePatch(context,filePath);
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
    public static boolean verifyDownloadFileCorrect(String urlMd5,File downloadFile) {
        String downloadMd5 = SharePatchFileUtil.getMD5(downloadFile);
        Log.d(TAG,"返回的urlMd5 = ".concat(urlMd5).concat(" ;自己拿到的urlMd5 =  ").concat(downloadMd5));
        return !TextUtils.isEmpty(urlMd5) && !TextUtils.isEmpty(downloadMd5) && TextUtils.equals(urlMd5,downloadMd5);
    }

    /**
     * 校验用户安装的包与下载的patch里面TINKER_ID是否一致，一致则可以下载，不一致则不能下载
     * @param manifestTinkerId
     * @param patchTinkerId
     * @return
     */
    public static boolean checkTinkerIdIsMatch(String manifestTinkerId, String patchTinkerId){
        return !TextUtils.isEmpty(patchTinkerId) && !TextUtils.isEmpty(manifestTinkerId) && TextUtils.equals(manifestTinkerId,patchTinkerId);
    }

}

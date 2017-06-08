package com.kn.fui.tinkerdemo.app;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Environment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.kn.fui.tinkerdemo.R;
import com.kn.fui.tinkerlib.util.IOUtils;
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import org.json.JSONObject;

import java.io.File;
import java.util.HashMap;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button button,button2,button3,button4,button5;

    private ImageView iv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button5 = (Button) findViewById(R.id.button5);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
        button5.setOnClickListener(this);
        iv = (ImageView) findViewById(R.id.iv1);

        iv.setImageResource(R.drawable.iv2);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){

            case R.id.button: // 加载
                String path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/patch_signed_7zip.apk";
                File file = new File(path);
                if(file.exists()){
                    TinkerInstaller.onReceiveUpgradePatch(getApplicationContext(),path);

                    String manifestTinkerId = ShareTinkerInternals.getManifestTinkerID(getApplicationContext());


                }
                break;

            case R.id.button2: //测试
                Toast.makeText(this, "来自补丁5", Toast.LENGTH_SHORT).show();
                Log.i("MainActivity","再测试一把");
//                Log.i("MainActivity","再测试一把");
                break;

            case R.id.button3: // 回滚
                Tinker.with(getApplicationContext()).cleanPatch();
                Toast.makeText(this, "回滚完成，请重启应用", Toast.LENGTH_SHORT).show();
                break;

            case R.id.button4: //  JavaJs互调
                Intent javaJsIntent = new Intent(this,JavaJsActivity.class);
                startActivity(javaJsIntent);
                break;

            case R.id.button5: //请求服务器
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        String URL = "null";
                        HashMap<String,String> params = new HashMap<>();
                        params.put("platform","MST_6A838");
                        params.put("supportvr","1");
                        params.put("method","bftv.voice.remind");
                        params.put("version","2.0");
                        params.put("softid","11170402");
                        params.put("softid","11170402");
                        params.put("apptoken","282340ce12c5e10fa84171660a2054f8");
                        params.put("from","bftv_android");
                        params.put("requestplatform","tv");
                        params.put("requestplatform","tv");
                        params.put("sys_version","V1.0.19");
                        JSONObject jsonObject = IOUtils.getJSONObject(params,URL);
                        Log.i(TAG,jsonObject.toString());
                    }
                }).start();
                break;

        }
    }
}

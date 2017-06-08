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
import com.tencent.tinker.lib.tinker.Tinker;
import com.tencent.tinker.lib.tinker.TinkerInstaller;
import com.tencent.tinker.loader.shareutil.ShareTinkerInternals;

import java.io.File;

public class MainActivity extends AppCompatActivity implements View.OnClickListener{

    public static final String TAG = MainActivity.class.getSimpleName();

    private Button button,button2,button3,button4;

    private ImageView iv;
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        button = (Button) findViewById(R.id.button);
        button2 = (Button) findViewById(R.id.button2);
        button3 = (Button) findViewById(R.id.button3);
        button4 = (Button) findViewById(R.id.button4);
        button.setOnClickListener(this);
        button2.setOnClickListener(this);
        button3.setOnClickListener(this);
        button4.setOnClickListener(this);
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

        }
    }
}

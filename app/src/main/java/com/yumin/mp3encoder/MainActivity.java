package com.yumin.mp3encoder;

import android.Manifest;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;


// 注意这知识个demo 正常情况应该把界面和业务分离
public class MainActivity extends AppCompatActivity {

    private final static int AUDIO_SAMPLE_RATE = 16000;
    private final static int REQUEST_PERMISSION_CODE = 100000;

    private String[] requestPermissions = new String[]{
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
    };


    private TextView tv;
    private SharedPreferences preferences;
    private boolean isFirstStart = true;
    private boolean requestPermissioning = false;

    private Mp3Encoder encoder;
    private String pcmPath;
    private String mp3Path;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        preferences= getSharedPreferences("config",MODE_PRIVATE);
        isFirstStart = preferences.getBoolean("isFirst_start",true);

        // Example of a call to a native method
        tv = (TextView) findViewById(R.id.sample_text);
        tv.setText(stringFromJNI());
        tv.setEnabled(false);
    }

    @Override
    protected void onResume() {
        super.onResume();
        setup();
    }

    private void setup(){
        if (!requestPermissioning) {
            List<String> needRequest = new ArrayList<>();
            for (String permission :requestPermissions) {
                if(checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
                    needRequest.add(permission);
                }
            }

            if (needRequest.size()>0) {
                requestPermissions((String[]) needRequest.toArray(),REQUEST_PERMISSION_CODE);
                requestPermissioning = true;
            } else {
                init();
            }
        }

    }

    private void init(){
        if (!isFirstStart) {
            encoder = new Mp3Encoder();
            tv.setEnabled(true);
            tv.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    new AsyncTask<Void, Void, Void>() {
                        @Override
                        protected Void doInBackground(Void... voids) {
                            pcmPath = new File(getFilesDir(),"16k.pcm").getAbsolutePath();
                            mp3Path = new File(getFilesDir(),"16k.mp3").getAbsolutePath();
                            File mp3File = new File(mp3Path);
                            if (mp3File.exists()) mp3File.delete();
                            File pcmFile = new File(pcmPath);
                            if (!pcmFile.exists() || pcmFile.isDirectory()) {
                                backgroundInit();
                            }
                            encoder.encodeInit(pcmPath,mp3Path, AUDIO_SAMPLE_RATE,2,2);
                            encoder.encod();
                            return null;
                        }

                        @Override
                        protected void onPostExecute(Void aVoid) {
                            super.onPostExecute(aVoid);
                            encoder.nativeEndEncod();//多线程问题
                        }
                    }.execute();
                }
            });


        } else {
            // 此处应该由加载动作
            AsyncTask<Void,Void,Void> initTask = new AsyncTask<Void, Void, Void>() {
                @Override
                protected Void doInBackground(Void... voids) {
                    backgroundInit();
                    return null;
                }

                @Override
                protected void onPostExecute(Void aVoid) {
                    super.onPostExecute(aVoid);
                    init();
                }
            }.execute();
        }
    }

    private void backgroundInit() {
        int length;
        byte[] buffer = new byte[1024];
        InputStream in =null;
        FileOutputStream outputStream = null;

        try {
            in = getAssets().open("16k.pcm");
            outputStream = new FileOutputStream(new File(getFilesDir(),"16k.pcm"));
            while(( length = in.read(buffer))>0){
                outputStream.write(buffer,0,length);
            }
            isFirstStart = false;
            preferences.edit().putBoolean("isFirst_start",false).apply();
        } catch (IOException e) {
            finish();
        } finally {
            IOUtils.closeQuietly(in);
            IOUtils.closeQuietly(outputStream);
        }
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);

        if (REQUEST_PERMISSION_CODE == requestCode) {
            for (int i = 0; i < grantResults.length ; i++) {
                if (grantResults[i]!= PackageManager.PERMISSION_GRANTED) {
                    finish();
                    return;
                }
            }
            init();
            requestPermissioning = false;
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    /**
     * A native method that is implemented by the 'native-lib' native library,
     * which is packaged with this application.
     */
    public native String stringFromJNI();

    // Used to load the 'native-lib' library on application startup.
    static {
        System.loadLibrary("native-lib");
    }
}

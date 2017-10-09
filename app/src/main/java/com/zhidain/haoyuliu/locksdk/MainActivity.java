package com.zhidain.haoyuliu.locksdk;


import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;

import com.zhidain.haoyuliu.sdklibrary.WekerLockLibrary;
import com.zhidain.haoyuliu.sdklibrary.bluetooth.CallBack;
import com.zhidain.haoyuliu.sdklibrary.model.LockModel;


public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";
    private Button btn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        WekerLockLibrary.getInstance().init(this);
        btn = (Button) findViewById(R.id.btn_start);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e(TAG, "onClick: " );
                WekerLockLibrary.getInstance().addLock(MainActivity.this, "18768122165", new CallBack<LockModel>() {
                    @Override
                    public void onSuccess(LockModel result) {
                        Log.e(TAG, "onSuccess: "+result.toString() );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }
                });
            }
        });
        findViewById(R.id.btn_open).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WekerLockLibrary.getInstance().openLock(MainActivity.this, "7829990123", new CallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "onSuccess: "+result);
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }
                });
            }
        });
        findViewById(R.id.btn_pwd).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                WekerLockLibrary.getInstance().setPassWord(MainActivity.this, "7829990123", "1", "", "", "1", "123456", new CallBack<String>() {
                    @Override
                    public void onSuccess(String result) {
                        Log.e(TAG, "onSuccess: "+result );
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "onError: "+e.getMessage() );
                    }
                });
            }
        });
    }
}

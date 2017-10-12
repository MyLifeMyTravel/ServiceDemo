package com.apkbus.servicedemo;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.apkbus.servicedemo.service.BoundService;

public class BoundServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = BoundServiceActivity.class.getSimpleName();

    private BoundService.CalcBinder mBinder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bound_service);

        findViewById(R.id.btn_bind_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this);
        findViewById(R.id.btn_plus).setOnClickListener(this);
        findViewById(R.id.btn_sub).setOnClickListener(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mServiceConnection = null;
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_bind_service:
                bindService(new Intent(this, BoundService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                //unbindService之后依然能调用plus和sub方法
                //因为Binder对象依然存在
                unbindService(mServiceConnection);
                break;
            case R.id.btn_plus:
                Toast.makeText(this, "1 + 2 = " + mBinder.plus(1, 2), Toast.LENGTH_SHORT).show();
                break;
            case R.id.btn_sub:
                Toast.makeText(this, "10 - 1 = " + mBinder.plus(10, 1), Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
            Log.d(TAG, TAG + " onServiceConnected");
            mBinder = (BoundService.CalcBinder) iBinder;
        }

        @Override
        public void onServiceDisconnected(ComponentName componentName) {
            Log.d(TAG, TAG + " onServiceDisconnected");
        }
    };
}

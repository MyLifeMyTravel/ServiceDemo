package com.apkbus.servicedemo;

import android.app.Service;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;

import com.apkbus.servicedemo.service.LifeCycleService;

/**
 * Created by littlejie on 2017/10/13.
 */

public class ServiceLifeCycleActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = ServiceLifeCycleActivity.class.getSimpleName();

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_service_lifecycle);

        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_bind_service).setOnClickListener(this);
        findViewById(R.id.btn_unbind_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.btn_start_service:
                Log.d(TAG, "Click to startService");
                startService(new Intent(this, LifeCycleService.class));
                break;
            case R.id.btn_bind_service:
                Log.d(TAG, "Click to bindService");
                bindService(new Intent(this, LifeCycleService.class), mServiceConnection, Service.BIND_AUTO_CREATE);
                break;
            case R.id.btn_unbind_service:
                Log.d(TAG, "Click to unbindService");
                unbindService(mServiceConnection);
                break;
            case R.id.btn_stop_service:
                Log.d(TAG, "Click to stopService");
                stopService(new Intent(this, LifeCycleService.class));
                break;
            default:
                break;
        }
    }

    private ServiceConnection mServiceConnection = new ServiceConnection() {
        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "onServiceConnected");
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.d(TAG, "onServiceDisconnected");
        }
    };
}

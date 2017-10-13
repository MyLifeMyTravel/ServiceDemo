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

import com.apkbus.servicedemo.service.ForegroundService;

public class ForegroundServiceActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_foregroud);

        findViewById(R.id.btn_start_foreground_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_foreground_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_foreground_service:
                startService(new Intent(this, ForegroundService.class));
                break;
            case R.id.btn_stop_foreground_service:
                stopService(new Intent(this, ForegroundService.class));
                break;
            default:
                break;
        }
    }

}
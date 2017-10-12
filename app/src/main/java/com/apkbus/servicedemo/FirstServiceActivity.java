package com.apkbus.servicedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.apkbus.servicedemo.service.FirstService;

public class FirstServiceActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = FirstServiceActivity.class.getSimpleName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_first_service);

        //绑定点击事件监听
        findViewById(R.id.btn_start_service).setOnClickListener(this);
        findViewById(R.id.btn_stop_service).setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.btn_start_service:
                //输出当前线程的ID
                Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId());
                //启动服务
                startService(new Intent(this, FirstService.class));
                break;
            case R.id.btn_stop_service:
                //关闭服务
                stopService(new Intent(this, FirstService.class));
                break;
        }
    }
}

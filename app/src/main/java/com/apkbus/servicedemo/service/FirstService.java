package com.apkbus.servicedemo.service;

import android.app.IntentService;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

public class FirstService extends IntentService {

    private static final String TAG = FirstService.class.getSimpleName();
    private static final int MESSAGE_SLEEP = 1;
    private static final int MESSAGE_GET_UP = 2;

    //主线程上的Handler，用于更新UI
    private final Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SLEEP:
                    Toast.makeText(getApplicationContext(), "让我先睡20s", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_GET_UP:
                    Toast.makeText(getApplicationContext(), "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show();
                    break;
            }
        }
    };

    public FirstService() {
        //IntentService构造方法需要有name参数，用于设置子线程的名称，方便调试
        //建议传类名
        super(TAG);
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        Log.d(TAG, "onHandleIntent, 当前线程ID：" + Thread.currentThread().getId());
        //注意，onHandleIntent 是在子线程中执行，所以请不要在此更新UI
        mMainHandler.sendEmptyMessage(MESSAGE_SLEEP);
        long startTime = System.currentTimeMillis();
        //模拟耗时操作
        try {
            Thread.sleep(20000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭");
        mMainHandler.sendEmptyMessage(MESSAGE_GET_UP);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }
}

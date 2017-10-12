package com.apkbus.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.util.Log;
import android.widget.Toast;

public class FirstService extends Service {

    private static final String TAG = FirstService.class.getSimpleName();
    private static final int MESSAGE_SLEEP = 1;
    private static final int MESSAGE_GET_UP = 2;

    //用于处理耗时操作的Handler
    private Handler mServiceHandler;
    private HandlerThread mServiceThread;
    //主线程上的Handler，用于更新UI
    private final Handler mMainHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MESSAGE_SLEEP:
                    Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId());
                    Toast.makeText(getApplicationContext(), "让我先睡20s", Toast.LENGTH_SHORT).show();
                    break;
                case MESSAGE_GET_UP:
                    Toast.makeText(getApplicationContext(), "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show();
                    //后台操作结束，主动关闭
                    //之后会回调onDestroy()
                    stopSelf();
                    break;
            }
        }
    };

    public FirstService() {
    }

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + " onBind");
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAG + " onUnbind");
        return super.onUnbind(intent);
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
        mServiceThread = new HandlerThread("FirstService", Process.THREAD_PRIORITY_BACKGROUND);
        mServiceThread.start();

        Looper serviceLooper = mServiceThread.getLooper();
        mServiceHandler = new ServiceHandler(serviceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Log.d(TAG, TAG + " onStartCommand");
        mServiceHandler.sendEmptyMessage(MESSAGE_SLEEP);
        return super.onStartCommand(intent, flags, startId);
    }

    private class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "当前线程ID：" + Thread.currentThread().getId() + "；即将开始耗时操作");
            //发送消息提示主线程弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_SLEEP);
            long startTime = System.currentTimeMillis();
            //模拟耗时操作
            try {
                Thread.sleep(20000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            //发送消息提示主线程耗时操作已结束，弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_GET_UP);
            Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭");
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, TAG + " onDestroy");
    }

}

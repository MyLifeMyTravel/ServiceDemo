package com.apkbus.servicedemo.service;

import android.app.Notification;
import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.Handler;
import android.os.IBinder;
import android.support.v4.app.NotificationCompat;
import android.util.Log;

import com.apkbus.servicedemo.R;

import java.util.Calendar;

public class ForegroundService extends Service {

    private static final String TAG = ForegroundService.class.getSimpleName();
    private static final int NOTIFICATION_ID = 1;

    private Handler mHandler = new Handler();

    public ForegroundService() {
    }

    @Override
    public void onCreate() {
        super.onCreate();
        Log.d(TAG, TAG + " onCreate");
        mHandler.post(mUpdateRunnable);
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
        //移除前台通知
        stopForeground(true);
    }

    @Override
    public IBinder onBind(Intent intent) {
        // TODO: Return the communication channel to the service.
        throw new UnsupportedOperationException("Not yet implemented");
    }

    private Notification buildNotification() {
        return new NotificationCompat.Builder(this)
                .setContentTitle("Foreground Service Notification")
                .setContentText("当前时间 : " + String.format("%tc", Calendar.getInstance()))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build();
    }

    private Runnable mUpdateRunnable = new Runnable() {
        @Override
        public void run() {
            mHandler.postDelayed(this, 1000);
            startForeground(NOTIFICATION_ID, buildNotification());
        }
    };
}

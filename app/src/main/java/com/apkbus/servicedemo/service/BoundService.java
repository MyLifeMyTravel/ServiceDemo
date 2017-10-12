package com.apkbus.servicedemo.service;

import android.app.Service;
import android.content.Intent;
import android.os.Binder;
import android.os.IBinder;
import android.util.Log;

public class BoundService extends Service {

    private static final String TAG = BoundService.class.getSimpleName();

    private CalcBinder mBinder = new CalcBinder();

    public BoundService() {
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

    @Override
    public IBinder onBind(Intent intent) {
        Log.d(TAG, TAG + " onBind");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        Log.d(TAG, TAG + " onUnbind");
        return super.onUnbind(intent);
    }

    public class CalcBinder extends Binder {

        public int plus(int a, int b) {
            Log.d(TAG, "a + b = " + (a + b));
            return a + b;
        }

        public int sub(int a, int b) {
            Log.d(TAG, "a - b = " + (a - b));
            return a - b;
        }
    }
}

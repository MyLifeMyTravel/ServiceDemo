package com.apkbus.servicedemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class FirstService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, TAG + " onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, TAG + " onStartCommand")
        Log.d(TAG, "当前线程ID：" + Thread.currentThread().id)
        //让 Service 所在的线程 sleep 10秒
        //如果为主线程，则会出现 ANR
        try {
            Thread.sleep(10000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, TAG + " onDestroy")
    }

    companion object {

        // 如果对 TAG 没有特殊要求，可以使用该方法获取类名当 TAG
        private val TAG = FirstService::class.java.simpleName
    }
}

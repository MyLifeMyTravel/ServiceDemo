package com.apkbus.servicedemo.service

import android.app.Service
import android.content.Intent
import android.os.IBinder
import android.util.Log

class LifeCycleService : Service() {

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, "onBind")
        return null
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, "onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, "onStartCommand,startId = " + startId)
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, "onUnbind")
        return super.onUnbind(intent)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, "onDestroy")
    }

    companion object {

        private val TAG = LifeCycleService::class.java.simpleName
    }
}

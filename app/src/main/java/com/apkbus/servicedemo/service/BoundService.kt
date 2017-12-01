package com.apkbus.servicedemo.service

import android.app.Service
import android.content.Intent
import android.os.Binder
import android.os.IBinder
import android.util.Log

class BoundService : Service() {

    private val mBinder = CalcBinder()

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, TAG + " onCreate")
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, TAG + " onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, TAG + " onDestroy")
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, TAG + " onBind")
        return mBinder
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, TAG + " onUnbind")
        return super.onUnbind(intent)
    }

    inner class CalcBinder : Binder() {

        fun plus(a: Int, b: Int): Int {
            Log.d(TAG, "a + b = " + (a + b))
            return a + b
        }

        fun sub(a: Int, b: Int): Int {
            Log.d(TAG, "a - b = " + (a - b))
            return a - b
        }
    }

    companion object {

        private val TAG = BoundService::class.java.simpleName
    }
}

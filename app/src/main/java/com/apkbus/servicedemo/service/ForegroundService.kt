package com.apkbus.servicedemo.service

import android.app.Notification
import android.app.Service
import android.content.Intent
import android.os.Handler
import android.os.IBinder
import android.support.v4.app.NotificationCompat
import android.util.Log
import com.apkbus.servicedemo.R
import java.util.*

class ForegroundService : Service() {

    private val mHandler = Handler()

    private val mUpdateRunnable = object : Runnable {
        override fun run() {
            mHandler.postDelayed(this, 1000)
            startForeground(NOTIFICATION_ID, buildNotification())
        }
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, TAG + " onCreate")
        mHandler.post(mUpdateRunnable)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, TAG + " onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, TAG + " onDestroy")
        //移除前台通知
        stopForeground(true)
    }

    override fun onBind(intent: Intent): IBinder? {
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    private fun buildNotification(): Notification {
        return NotificationCompat.Builder(this, TAG)
                .setContentTitle("Foreground Service Notification")
                .setContentText("当前时间 : " + String.format("%tc", Calendar.getInstance()))
                .setSmallIcon(R.mipmap.ic_launcher)
                .build()
    }

    companion object {

        private val TAG = ForegroundService::class.java.simpleName
        private val NOTIFICATION_ID = 1
    }
}

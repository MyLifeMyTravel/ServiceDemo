package com.apkbus.servicedemo.service

import android.annotation.SuppressLint
import android.app.IntentService
import android.content.Intent
import android.os.Handler
import android.os.Message
import android.util.Log
import android.widget.Toast

//IntentService构造方法需要有name参数，用于设置子线程的名称，方便调试
//建议传类名
class FirstService : IntentService(TAG) {

    //主线程上的Handler，用于更新UI
    @SuppressLint("HandlerLeak")
    private val mMainHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_SLEEP -> Toast.makeText(applicationContext, "让我先睡20s", Toast.LENGTH_SHORT).show()
                MESSAGE_GET_UP -> Toast.makeText(applicationContext, "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onHandleIntent(intent: Intent?) {
        Log.d(TAG, "onHandleIntent, 当前线程ID：" + Thread.currentThread().id)
        //注意，onHandleIntent 是在子线程中执行，所以请不要在此更新UI
        mMainHandler.sendEmptyMessage(MESSAGE_SLEEP)
        val startTime = System.currentTimeMillis()
        //模拟耗时操作
        try {
            Thread.sleep(20000)
        } catch (e: InterruptedException) {
            e.printStackTrace()
        }

        Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭")
        mMainHandler.sendEmptyMessage(MESSAGE_GET_UP)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, TAG + " onCreate")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.d(TAG, TAG + " onStartCommand")
        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.d(TAG, TAG + " onDestroy")
    }

    companion object {

        private val TAG = FirstService::class.java.simpleName
        private val MESSAGE_SLEEP = 1
        private val MESSAGE_GET_UP = 2
    }
}

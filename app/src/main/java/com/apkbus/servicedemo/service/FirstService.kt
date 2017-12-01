package com.apkbus.servicedemo.service

import android.annotation.SuppressLint
import android.app.Service
import android.content.Intent
import android.os.*
import android.util.Log
import android.widget.Toast

class FirstService : Service() {

    //用于处理耗时操作的Handler
    private var mServiceHandler: Handler? = null
    private var mServiceThread: HandlerThread? = null
    //主线程上的Handler，用于更新UI
    @SuppressLint("HandlerLeak")
    private val mMainHandler = object : Handler() {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            when (msg.what) {
                MESSAGE_SLEEP -> {
                    Log.d(TAG, "当前线程ID：" + Thread.currentThread().id)
                    Toast.makeText(applicationContext, "让我先睡20s", Toast.LENGTH_SHORT).show()
                }
                MESSAGE_GET_UP -> {
                    Toast.makeText(applicationContext, "我睡醒了~服务关闭", Toast.LENGTH_SHORT).show()
                    //后台操作结束，主动关闭
                    //之后会回调onDestroy()
                    stopSelf()
                }
            }
        }
    }

    override fun onBind(intent: Intent): IBinder? {
        Log.d(TAG, TAG + " onBind")
        // TODO: Return the communication channel to the service.
        throw UnsupportedOperationException("Not yet implemented")
    }

    override fun onUnbind(intent: Intent): Boolean {
        Log.d(TAG, TAG + " onUnbind")
        return super.onUnbind(intent)
    }

    override fun onCreate() {
        super.onCreate()
        Log.d(TAG, TAG + " onCreate")
        mServiceThread = HandlerThread("FirstService", Process.THREAD_PRIORITY_BACKGROUND)
        mServiceThread!!.start()

        val serviceLooper = mServiceThread!!.looper
        mServiceHandler = ServiceHandler(serviceLooper)
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.d(TAG, TAG + " onStartCommand")
        mServiceHandler!!.sendEmptyMessage(MESSAGE_SLEEP)
        return super.onStartCommand(intent, flags, startId)
    }

    private inner class ServiceHandler(looper: Looper) : Handler(looper) {

        override fun handleMessage(msg: Message) {
            super.handleMessage(msg)
            Log.d(TAG, "当前线程ID：" + Thread.currentThread().id + "；即将开始耗时操作")
            //发送消息提示主线程弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_SLEEP)
            val startTime = System.currentTimeMillis()
            //模拟耗时操作
            try {
                Thread.sleep(20000)
            } catch (e: InterruptedException) {
                e.printStackTrace()
            }

            //发送消息提示主线程耗时操作已结束，弹出Toast
            mMainHandler.sendEmptyMessage(MESSAGE_GET_UP)
            Log.d(TAG, "耗时操作已结束，用时：" + (System.currentTimeMillis() - startTime) / 1000 + "s，该服务即将关闭")
        }
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

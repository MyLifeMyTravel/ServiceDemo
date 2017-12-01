package com.apkbus.servicedemo

import android.app.Service
import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View

import com.apkbus.servicedemo.service.LifeCycleService

/**
 * Created by littlejie on 2017/10/13.
 */

class ServiceLifeCycleActivity : AppCompatActivity(), View.OnClickListener {

    private val mServiceConnection = object : ServiceConnection {
        override fun onServiceConnected(name: ComponentName, service: IBinder) {
            Log.d(TAG, "onServiceConnected")
        }

        override fun onServiceDisconnected(name: ComponentName) {
            Log.d(TAG, "onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_lifecycle)

        findViewById<View>(R.id.btn_start_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_bind_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_unbind_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_stop_service).setOnClickListener(this)
    }

    override fun onClick(v: View) {
        when (v.id) {
            R.id.btn_start_service -> {
                Log.d(TAG, "Click to startService")
                startService(Intent(this, LifeCycleService::class.java))
            }
            R.id.btn_bind_service -> {
                Log.d(TAG, "Click to bindService")
                bindService(Intent(this, LifeCycleService::class.java), mServiceConnection, Service.BIND_AUTO_CREATE)
            }
            R.id.btn_unbind_service -> {
                Log.d(TAG, "Click to unbindService")
                unbindService(mServiceConnection)
            }
            R.id.btn_stop_service -> {
                Log.d(TAG, "Click to stopService")
                stopService(Intent(this, LifeCycleService::class.java))
            }
            else -> {
            }
        }
    }

    companion object {

        private val TAG = ServiceLifeCycleActivity::class.java.simpleName
    }
}

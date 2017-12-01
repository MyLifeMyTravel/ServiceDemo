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
import android.widget.Toast
import com.apkbus.servicedemo.service.BoundService

class BoundServiceActivity : AppCompatActivity(), View.OnClickListener {

    private var mBinder: BoundService.CalcBinder? = null

    private var mServiceConnection: ServiceConnection? = object : ServiceConnection {
        override fun onServiceConnected(componentName: ComponentName, iBinder: IBinder) {
            Log.d(TAG, TAG + " onServiceConnected")
            mBinder = iBinder as BoundService.CalcBinder
        }

        override fun onServiceDisconnected(componentName: ComponentName) {
            Log.d(TAG, TAG + " onServiceDisconnected")
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_bound_service)

        findViewById<View>(R.id.btn_bind_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_unbind_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_plus).setOnClickListener(this)
        findViewById<View>(R.id.btn_sub).setOnClickListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
        mServiceConnection = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_bind_service -> bindService(Intent(this, BoundService::class.java), mServiceConnection, Service.BIND_AUTO_CREATE)
            R.id.btn_unbind_service ->
                //unbindService之后依然能调用plus和sub方法
                //因为Binder对象依然存在
                unbindService(mServiceConnection)
            R.id.btn_plus -> Toast.makeText(this, "1 + 2 = " + mBinder!!.plus(1, 2), Toast.LENGTH_SHORT).show()
            R.id.btn_sub -> Toast.makeText(this, "10 - 1 = " + mBinder!!.sub(10, 1), Toast.LENGTH_SHORT).show()
            else -> {
            }
        }
    }

    companion object {

        private val TAG = BoundServiceActivity::class.java.simpleName
    }
}

package com.apkbus.servicedemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import com.apkbus.servicedemo.service.FirstService

class FirstServiceActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_first_service)

        //绑定点击事件监听
        findViewById<View>(R.id.btn_start_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_stop_service).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_service -> {
                //输出当前线程的ID
                Log.d(TAG, "当前线程ID：" + Thread.currentThread().id)
                //启动服务
                startService(Intent(this, FirstService::class.java))
            }
            R.id.btn_stop_service ->
                //关闭服务
                stopService(Intent(this, FirstService::class.java))
        }
    }

    companion object {

        private val TAG = FirstServiceActivity::class.java.simpleName
    }
}

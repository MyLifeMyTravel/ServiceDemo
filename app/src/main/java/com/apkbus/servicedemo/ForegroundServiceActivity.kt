package com.apkbus.servicedemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import com.apkbus.servicedemo.service.ForegroundService

class ForegroundServiceActivity : AppCompatActivity(), View.OnClickListener {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_foregroud)

        findViewById<View>(R.id.btn_start_foreground_service).setOnClickListener(this)
        findViewById<View>(R.id.btn_stop_foreground_service).setOnClickListener(this)
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.btn_start_foreground_service -> startService(Intent(this, ForegroundService::class.java))
            R.id.btn_stop_foreground_service -> stopService(Intent(this, ForegroundService::class.java))
            else -> {
            }
        }
    }

}
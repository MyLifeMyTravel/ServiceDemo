package com.apkbus.servicedemo

import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.ListView
import java.util.*

class MainActivity : AppCompatActivity() {

    private var mListView: ListView? = null
    private var mAdapter: ArrayAdapter<String>? = null
    private var mItemList: MutableList<ItemInfo>? = null

    private val allItem: List<String>
        get() {
            return mItemList!!.map { it.item }
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        initItem()
        initView()
        initListener()
    }

    private fun initItem() {
        mItemList = ArrayList()
        mItemList!!.add(ItemInfo("FirstService", FirstServiceActivity::class.java))
        mItemList!!.add(ItemInfo("BoundService", BoundServiceActivity::class.java))
    }

    private fun initView() {
        mListView = findViewById<View>(R.id.list) as ListView
        mAdapter = ArrayAdapter(this, android.R.layout.simple_list_item_1)
        mAdapter!!.addAll(allItem)
        mListView!!.adapter = mAdapter
    }

    private fun initListener() {
        mListView!!.onItemClickListener = AdapterView.OnItemClickListener { _, _, i, _ -> startActivity(Intent(this@MainActivity, mItemList!![i].clazz)) }
    }

    private inner class ItemInfo(var item: String, var clazz: Class<out AppCompatActivity>)
}

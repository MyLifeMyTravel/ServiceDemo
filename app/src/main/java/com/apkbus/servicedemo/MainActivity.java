package com.apkbus.servicedemo;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity {

    private ListView mListView;
    private ArrayAdapter<String> mAdapter;
    private List<ItemInfo> mItemList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        initItem();
        initView();
        initListener();
    }

    private void initItem() {
        mItemList = new ArrayList<>();
        mItemList.add(new ItemInfo("FirstService", FirstServiceActivity.class));
    }

    private void initView() {
        mListView = (ListView) findViewById(R.id.list);
        mAdapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1);
        mAdapter.addAll(getAllItem());
        mListView.setAdapter(mAdapter);
    }

    private void initListener() {
        mListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int i, long l) {
                startActivity(new Intent(MainActivity.this, mItemList.get(i).getClazz()));
            }
        });
    }

    private List<String> getAllItem() {
        List<String> itemList = new ArrayList<>();
        for (ItemInfo item : mItemList) {
            itemList.add(item.getItem());
        }
        return itemList;
    }

    private class ItemInfo {
        private String item;
        private Class<? extends AppCompatActivity> clazz;

        public ItemInfo(String item, Class<? extends AppCompatActivity> clazz) {
            this.item = item;
            this.clazz = clazz;
        }

        public String getItem() {
            return item;
        }

        public void setItem(String item) {
            this.item = item;
        }

        public Class<? extends AppCompatActivity> getClazz() {
            return clazz;
        }

        public void setClazz(Class<? extends AppCompatActivity> clazz) {
            this.clazz = clazz;
        }
    }
}

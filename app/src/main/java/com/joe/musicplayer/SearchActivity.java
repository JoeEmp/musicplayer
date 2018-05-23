package com.joe.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.SearchView;

import java.util.List;

/**
 * Created by joe on 2018/5/22.
 */

public class SearchActivity extends Activity {
    ListView Listview;
    SearchView Searchview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        Searchview= (SearchView) findViewById(R.id.searchview_ser);
        Listview = (ListView) findViewById(R.id.listview_ser);
        Listview.setTextFilterEnabled(true);

        Intent it=getIntent();
        List mylist= (List) it.getSerializableExtra("musiclist");

        //剪掉路径
        for(int i=0,len=mylist.size();i<len;i++){
            mylist.set(i,mylist.get(i).toString().substring(mylist.get(i).toString().lastIndexOf("/") + 1));    //剪掉路径
        }

        ArrayAdapter<List> adapter = new ArrayAdapter<List>(SearchActivity.this, android.R.layout.simple_list_item_1, mylist);
        Listview.setAdapter(adapter);
        Listview.setTextFilterEnabled(true);

        Intent it_def=new Intent(SearchActivity.this,MainActivity.class);
        it_def.putExtra("index",-1);
        setResult(RESULT_OK,it_def);

        //列表点击侦听
        Listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view , int position , long id){
                //返回数据
                Intent it2=new Intent(SearchActivity.this,MainActivity.class);
                it2.putExtra("index",position);
                setResult(RESULT_OK,it2);
                finish();
//            Toast.makeText(ListActivity.this,position+" item",Toast.LENGTH_LONG).show();
            }
        });

        // 设置搜索文本监听
        Searchview.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            // 当点击搜索按钮时触发该方法
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            // 当搜索内容改变时触发该方法
            @Override
            public boolean onQueryTextChange(String newText) {
                if (!TextUtils.isEmpty(newText)){
                    Listview.setFilterText(newText);
                }else{
                    Listview.clearTextFilter();
                }
                return false;
            }
        });
    }
}

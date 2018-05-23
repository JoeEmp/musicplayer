package com.joe.musicplayer;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;

import java.util.List;

/**
 * Created by joe on 2018/5/22.
 */

public class ListActivity extends Activity {
    ListView Listview;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_list);
        Listview = (ListView) findViewById(R.id.listview);

        Intent it=getIntent();
        List mylist= (List) it.getSerializableExtra("musiclist");

        //剪掉路径
        for(int i=0,len=mylist.size();i<len;i++){
            mylist.set(i,mylist.get(i).toString().substring(mylist.get(i).toString().lastIndexOf("/") + 1));    //剪掉路径
        }

        ArrayAdapter<List> adapter = new ArrayAdapter<List>(ListActivity.this, android.R.layout.simple_list_item_1, mylist);
        Listview.setAdapter(adapter);

        Intent it_def=new Intent(ListActivity.this,MainActivity.class);
        it_def.putExtra("index",-1);
        setResult(RESULT_OK,it_def);

        //listview点击侦听函数
        Listview.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view , int position , long id){
                //返回数据
                Intent it2=new Intent(ListActivity.this,MainActivity.class);
                it2.putExtra("index",position);
                setResult(RESULT_OK,it2);
                finish();
//            Toast.makeText(ListActivity.this,position+" item",Toast.LENGTH_LONG).show();
            }
        });

    }

}

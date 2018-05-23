package com.joe.musicplayer;

import android.content.Intent;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.SearchView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import java.io.File;
import java.io.IOException;
import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import static com.joe.musicplayer.MusicList.InitMP2;
import static com.joe.musicplayer.MusicList.getMusicData2;

public class MainActivity extends AppCompatActivity {
    //播放标志位
    public boolean flag = false;
    //控件标志
    public TextView SN;
    public TextView Current, Durtain;
    public Button Play, Pre, Next, List;
    public SeekBar Seekbar;

    public java.util.List<String> musiclist;
    public MediaPlayer mp = new MediaPlayer();      //媒体播放器
    String path = "/mnt";       //遍历路径
    int index = 0;              //列表索引
    Intent it1;
    Handler mHandler = new Handler();
    private final int UPDATE=0x01;
    private final int STOP=0x02;
    File file= Environment.getExternalStorageDirectory();
    //播放功能
    public void mpstate() {
        if (mp.getCurrentPosition() == 0)
            try {
                mp.prepare();
            } catch (IOException e) {
                Toast.makeText(this, "缓冲失败", Toast.LENGTH_LONG).show();
            }

        if (flag == false) {
            mp.start();
            mHandler.sendEmptyMessage(UPDATE);
            flag = true;
            Play.setBackgroundResource(R.drawable.pause);
            String title = musiclist.get(index).toString().substring(musiclist.get(index).toString().lastIndexOf("/") + 1);
            SN.setText(title);
            Durtain.setText(changeDuration(mp.getDuration()));
        }
        else {
            mp.pause();
            Play.setBackgroundResource(R.drawable.play);
            mHandler.sendEmptyMessage(STOP);
            flag = false;
        }
    }

    //播放
    public void play_or_pause(View V) {
        mpstate();
    }

    //下一首
    public void next(View V) {
        if (++index > musiclist.size() - 1)
            index = 0;
        try {
            mp.reset();
            mp.setDataSource(musiclist.get(index).toString());
        } catch (IOException e) {
            Toast.makeText(this, "数据源设置失败", Toast.LENGTH_LONG).show();
        }
        flag = false;
        mpstate();
    }

    //上一首
    public void pervious(View V) {
        if (--index < 0)
            index = musiclist.size() - 1;
        try {
            mp.reset();
            mp.setDataSource(musiclist.get(index).toString());
        } catch (IOException e) {
            Toast.makeText(this, "数据源设置失败", Toast.LENGTH_LONG).show();
        }
        flag = false;
        mpstate();
    }

    //播放模式
    public void mode(View V) {

    }

    //列表
    public void list(View V) {
        it1 = new Intent(MainActivity.this, ListActivity.class);
        it1.putExtra("musiclist", (Serializable) musiclist);
        MainActivity.this.startActivityForResult(it1,1);
        it1=null;
    }
    //activity返回值（选歌）
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK) {
            int def;
            def = data.getIntExtra("index", index);     //为防止ListView空针 index为正常值
            if(def==-1)     //无选中返回处理
                return;
            index=def;
        }
        try {
            mp.reset();
            mp.setDataSource(musiclist.get(index).toString());
        } catch (IOException e) {
            Toast.makeText(this, "数据源设置失败", Toast.LENGTH_LONG).show();
        }
        flag = false;
        mpstate();
    }

    //搜素
    public void search(){
        it1 = new Intent(MainActivity.this, SearchActivity.class);
        it1.putExtra("musiclist", (Serializable) musiclist);
        MainActivity.this.startActivityForResult(it1,1);
        it1=null;
    }

    //侦听歌曲是否播完
    private final class CompletionListener implements MediaPlayer.OnCompletionListener {

        @Override
        public void onCompletion(MediaPlayer mp) {
            if (++index > musiclist.size() - 1)
                index = 0;
            try {
                mp.reset();
                mp.setDataSource(musiclist.get(index).toString());
            } catch (IOException e) {
                Toast.makeText(MainActivity.this, "数据源设置失败", Toast.LENGTH_LONG).show();
            }
            flag = false;
            mpstate();
        }

    }
    //时间转换
    public static String changeDuration(int Duration){
        int musicTime = Duration / 1000;
        if(Duration%1000==0){
            if((musicTime+1)%60<10)
                return musicTime / 60 + ":0" + (musicTime+1) % 60;
            return musicTime / 60 + ":" + (musicTime+1) % 60;
        }
        else{
            if(musicTime%60<10)
                return musicTime / 60 + ":0" + musicTime % 60;
            return musicTime / 60 + ":" + musicTime % 60;
        }
    }

    //初始化控件
    private void init() {
        this.SN = (TextView) super.findViewById(R.id.SingName);
        this.Current = (TextView) super.findViewById(R.id.current);
        this.Durtain = (TextView) super.findViewById(R.id.end);
        this.Play = (Button) super.findViewById(R.id.play);
        this.Pre = (Button) super.findViewById(R.id.per);
        this.Next = (Button) super.findViewById(R.id.next);
        this.List = (Button) super.findViewById(R.id.list);
        this.Seekbar = (SeekBar) super.findViewById(R.id.seekbar);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        init();
        mp.setOnCompletionListener(new CompletionListener());

        //创建歌单
        try {
            musiclist = new ArrayList<String>();
            musiclist.clear();
            musiclist = getMusicData2(path, musiclist);
            mp = InitMP2(musiclist, this, mp);
        } catch (Exception e) {
            Toast.makeText(this, "遍历失败", Toast.LENGTH_LONG).show();
        }

        mHandler = new Handler() {

            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case UPDATE:
                        try {
                            Seekbar.setMax(mp.getDuration());
                            Seekbar.setProgress(mp.getCurrentPosition());
                            Current.setText(changeDuration(mp.getCurrentPosition()));
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //mHandler.sendEmptyMessageDelayed(UPDATE);
                        mHandler.sendEmptyMessageDelayed(UPDATE,1000);
                        break;
                    case STOP:
                        break;
                }
            }
        };
        Seekbar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            /**
             * 拖动条停止拖动的时候调用
             */
            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

            }
            /**
             * 拖动条开始拖动的时候调用
             */
            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }
            /**
             * 拖动条进度改变的时候调用
             */
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser){
                    mp.seekTo(seekBar.getProgress());
                    Current.setText(changeDuration(mp.getCurrentPosition()));
                }
            }
        });
        //Create ending
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_Search) {
            search();
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
    //class ending
}

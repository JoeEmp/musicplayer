package com.joe.musicplayer;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.media.MediaPlayer;
import android.provider.MediaStore;
import android.widget.Toast;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by joe on 2018/5/22.
 */

public class MusicList {
    //从安卓自身媒体数据库获得音频信息（未必会有）
    public static List<Music> getMusicData1(Context context)
    {
        List<Music> MusicList = new ArrayList<Music>();
        ContentResolver contentResolver =context.getContentResolver();
        if(contentResolver != null) {
            // 获取所有歌曲
            Cursor cursor = contentResolver.query(
                    MediaStore.Audio.Media.EXTERNAL_CONTENT_URI,
                    null,
                    null,
                    null,
                    MediaStore.Audio.Media.DEFAULT_SORT_ORDER);
            if(null== cursor){
                return null;
            }
            if(cursor.moveToFirst()) {
                do {
                    Music m = new Music();
                    String title = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.TITLE));
                    String singer = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ARTIST));
                    if("<unknown>".equals(singer)) {
                        singer = "未知艺术家";
                    }
                    String album = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.ALBUM));
                    long size = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.SIZE));
                    long duration = cursor.getLong(cursor.getColumnIndex(MediaStore.Audio.Media.DURATION));
                    String uri = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DATA));
                    String filename = cursor.getString(cursor.getColumnIndex(MediaStore.Audio.Media.DISPLAY_NAME));
                    String sbr = filename.substring(filename.length() - 3, filename.length());

                    if(sbr.equals("mp3")) {
                        m.setTitle(title);
                        m.setSinger(singer);
                        m.setAlbum(album);
                        m.setDuration(duration);
                        m.setSize(size);
                        m.setUri(uri);
                        MusicList.add(m);
                    }
                }
                while(cursor.moveToNext());
            }
        }
        return MusicList;
    }

    //遍历文件
    public static List<String> getMusicData2(String strpath,List musiclist){
        String filename;//文件名
        String suf;//文件后缀
        File dir = new File(strpath);//文件夹dir
        File[] files = dir.listFiles();//文件夹下的所有文件或文件夹

        if (files == null)
            return null;

        for (int i = 0; i < files.length; i++) {

            if (files[i].isDirectory())
            {
                //  System.out.println("---" + files[i].getAbsolutePath());
                getMusicData2(files[i].getAbsolutePath(),musiclist);        //递归文件夹！！！

            }
            else {
                filename = files[i].getName();
                int j = filename.lastIndexOf(".");
                suf = filename.substring(j+1);//得到文件后缀

                if(suf.equalsIgnoreCase("mp3"))      //判断是不是mp3后缀的文件
                    musiclist.add(files[i].getAbsolutePath());      //对于文件才把它的路径加到filelist中
            }
        }
        return musiclist;
    }

    public static MediaPlayer InitMP1(List<Music> list, Context context, MediaPlayer mp){
        String path=(list.get(0).getUri()+list.get(0).getFilename());   //绝对路径
        try {
            mp.setDataSource(path);
        }catch (Exception e){
            Toast.makeText(context,"路径错误",Toast.LENGTH_LONG).show();
        }
        return mp;
    }

    public static MediaPlayer InitMP2(List<String> list, Context context, MediaPlayer mp){
        try {
            mp.setDataSource(list.get(0));
        }catch (Exception e){
            Toast.makeText(context,"路径错误或目录下没有文件",Toast.LENGTH_LONG).show();
        }
        return mp;
    }
}
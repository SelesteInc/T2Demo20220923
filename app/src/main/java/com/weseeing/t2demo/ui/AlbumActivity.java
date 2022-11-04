package com.weseeing.t2demo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.GridView;

import com.weseeing.t2demo.DemoMainActivity;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.databinding.ActivityAlbumBinding;
import com.weseeing.t2demo.ui.adapter.GridAdapter;
import com.weseeing.t2demo.ui.adapter.MediaData;
import com.weseeing.t2demo.utils.Constant;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class AlbumActivity extends AppCompatActivity implements View.OnClickListener {

    private static final String TAG = "AlbumActivity";
    private ActivityAlbumBinding binding;


    /**
     * 相册
     */
    private GridView mAlbumGridView;
    private GridAdapter mAlbumAdapter;

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what){
                case 101:
                    mAlbumAdapter.updateAdapter(getFilesAllName(Constant.ROOT_PATH));
                    break;
            }
        }
    };

    public static void launch(Activity from) {
        Intent intent  = new Intent(from, AlbumActivity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityAlbumBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        //相册 photo album
        mAlbumAdapter = new GridAdapter(this);
        binding.gridView.setAdapter(mAlbumAdapter);
        binding.gridView.setOnItemClickListener(mItemListener);
        mAlbumAdapter.updateAdapter(getFilesAllName(Constant.ROOT_PATH));

        binding.btnRefreshAlbum.setOnClickListener(this);
        binding.btnDeleteAllAlbum.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btnDeleteAllAlbum:
                deleteAllFiles(Constant.ROOT_PATH,mHandler);
                break;
            case R.id.btnRefreshAlbum:
                mAlbumAdapter.updateAdapter(getFilesAllName(Constant.ROOT_PATH));
                break;
        }
    }

    private AdapterView.OnItemClickListener mItemListener = new AdapterView.OnItemClickListener() {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            MediaData mediaData = mAlbumAdapter.getItem(position);
            if (mediaData != null){
                if (mediaData.type == MediaData.TYPE_PICTURE){
                    PhotosActivity.launch(AlbumActivity.this,mediaData.getUrl());
                }else if(mediaData.type == MediaData.TYPE_VIDEO){
                    VideoPlayActivity.launch(AlbumActivity.this,mediaData.getName(),mediaData.getUrl());
                }
            }
        }
    };



    public static List<MediaData> getFilesAllName(String path){
        Log.d("TESTINGTAG","getFilesAllName path: " + path);
        List<MediaData> listMediaData = new ArrayList<MediaData>();
        File file = new File(path);
        File[] files = file.listFiles();
        if (files == null) {
            Log.e(TAG, "getFilesAllName null");
            return  listMediaData;
        }
        for (int i = 0; i < files.length; i++) {
            String name =  files[i].getName();
            if ( (!TextUtils.isEmpty(name)) && name.contains(".jpeg")){
                MediaData data = new MediaData();
                data.setType(MediaData.TYPE_PICTURE);
                data.setUrl(files[i].getAbsolutePath());
                data.setName(files[i].getName());
                listMediaData.add(data);
            }else {
                if ( (!TextUtils.isEmpty(name)) && name.contains(".mp4")){
                    MediaData data = new MediaData();
                    data.setUrl(files[i].getAbsolutePath());
                    data.setName(files[i].getName());
                    data.setType(MediaData.TYPE_VIDEO);
                    listMediaData.add(data);
                }
            }
        }

        Comparator<MediaData> comparator = new Comparator<MediaData>() {
            public int compare(MediaData s1, MediaData s2) {
                return s2.getName().compareTo(s1.getName());
            }
        };
        Collections.sort(listMediaData, comparator);
        Log.e(TAG,"listMediaData:"+listMediaData.size());
        return listMediaData;
    }


    public  void deleteAllFiles (final String filePath, final Handler handler){
        new Thread(new Runnable() {
            @Override
            public void run() {
                if (!TextUtils.isEmpty(filePath)) {
                    try {
                        File file = new File(filePath);
                        if (file.isDirectory()) {// 处理目录 Working with directories
                            File files[] = file.listFiles();
                            for (int i = 0; i < files.length; i++) {
                                Log.d(TAG,"files[i].getAbsolutePath()"+files[i].getAbsolutePath());
                                if (!files[i].isDirectory()) {// 如果是文件，删除 If it is a file, delete it
                                    Log.d(TAG,"isDirectory");
                                    files[i].delete();
                                }
                            }
                        }
                    } catch (Exception e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                }
                if (handler != null){
                    handler.sendEmptyMessage(101);
                }
            }
        }).start();
    }
}
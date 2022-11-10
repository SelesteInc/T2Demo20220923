package com.weseeing.t2demo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;

import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.t2demo.R;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadActivity extends AppCompatActivity {
    private final String TAG = "DownloadActivity";
    private static final String  ROOT_PATH = Environment.getExternalStorageDirectory().toString()+ File.separator + "T2/";

    private String mVideosXmlUrl = "http://%s/cgi-bin/filelist?type=videos"; //获取视频列表xml文件 Get video list xml file
    private String mPicturesXml = "http://%s/cgi-bin/filelist?type=pictures"; //获取图片列表xml文件 Get the picture list xml file

    private String mPictureDelete = "http://%s/cgi-bin/filedel?type=pictures&filename=%s" ; //删除单个图片 delete a single image
    private String mVideoDelete = "http://%s/cgi-bin/filedel?type=videos&filename=%s" ;  //删除单个视频 delete a single video

    private String  mPictureDownload ="http://%s/cgi-bin/download?type=pictures&filename=%s"; //下载单个图片 Download a single image
    private String  mVideoDownload ="http://%s/cgi-bin/download?type=videos&filename=%s"; //下载单个视频 Download a single video

    private String mIp;

    public static void launch(Activity from) {
        Log.d("TESTINGTAG","DownloadActivity, launch method reached");
        Intent intent  = new Intent(from, DownloadActivity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d("TESTINGTAG","DownloadActivity, onCreate method reached");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download);
        setTitle("下载");
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mIp = SeeDeviceChannel.create(getApplication(),SeeDeviceChannel.TYPE_WIFI_DEVICE,null).getAddress();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * 图片xml下载 image xml download
     * @param view
     */
    public void onPicturesList(View view){
        DownloadPicturesXml();
    }

    private boolean DownloadPicturesXml(){
        String url =  String.format(mPicturesXml,mIp);
        Log.d(TAG,"onPicList url:"+url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Log.d(TAG,"onPicList xxxxxurl:"+request.toString());

        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                    @Override
                    public void onFailure(Call call, IOException e) {
                        Log.e(TAG,"onPicListcccccccccccc:");
                    }

                    @Override
                    public void onResponse(Call call, Response response) throws IOException {
                        if(response.isSuccessful()){//内容太长日志打印有限制，可能出现打印不全 The content is too long, the log printing is limited, and the printing may be incomplete
                            InputStream in = null;
                            byte[] buffer = new byte[4096];
                            File cacheDir = new File(ROOT_PATH);// getCacheDir();
                            File xmlFile = new File(cacheDir, "SeeTPicturesMediaName.xml");
                            FileOutputStream fo = new FileOutputStream(xmlFile, false);
                            in = response.body().byteStream();
                            int n = 0;
                            while ((n = in.read(buffer)) != -1) {
                                fo.write(buffer, 0, n);
                            }
                            fo.close();
                            in.close();
//                            String result = response.body().string();
//                            Log.d(TAG,"onPicturesList result:"+result);
                        }
                    }
                });

            } catch (Exception e) {
            Log.e("xlh","e:"+e.toString());
                e.printStackTrace();
            }
        return false;
    }


    /**
     * 视频xml下载 video xml download
     * @param view
     */
    public void onVideosList(View view){
        DownloadVideosXml();
    }

    private boolean DownloadVideosXml(){
        String url =  String.format(mVideosXmlUrl,mIp);
        Log.d(TAG,"DownloadVideosXml url:"+url);

        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Log.d(TAG,"DownloadVideosXml xxxxxurl:"+request.toString());

        try {
            Call call = client.newCall(request);
            call.enqueue(new Callback() {
                @Override
                public void onFailure(Call call, IOException e) {
                }

                @Override
                public void onResponse(Call call, Response response) throws IOException {
                    if(response.isSuccessful()){//内容太长日志打印有限制，可能出现打印不全
                        InputStream in = null;

                        byte[] buffer = new byte[4096];
                        File cacheDir = new File(ROOT_PATH);// getCacheDir();
                        File xmlFile = new File(cacheDir, "SeeTVideosMediaName.xml");
                        FileOutputStream fo = new FileOutputStream(xmlFile, false);
                        in = response.body().byteStream();
                        int n = 0;
                        while ((n = in.read(buffer)) != -1) {
                            fo.write(buffer, 0, n);
                        }

                        fo.close();
                        in.close();
                        //String result = response.body().string();
                        //Log.d(TAG,"onPicturesList result:"+result);
                    }
                }
            });

        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }


    /**
     * 图片删除 image deletion
     * @param view
     */
    public void onPictureDelete(View view){
        //图片名称，测试请使用上面列表获取的文件名称 Image name, please use the file name obtained from the above list for testing
        String pictureName = "IMG_20200623_061113_33.jpeg";

        String url =  String.format(mPictureDelete,mIp,pictureName);
        Log.d(TAG,"onPictureDelete url:"+url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();

        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //...
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                    String result = response.body().string();
                    //处理UI需要切换到UI线程处理 Processing UI requires switching to UI thread processing
                    Log.d(TAG,"onPictureDelete result:"+result);
                }
            }
        });
    }

    /**
     * 视频删除 video deletion
     * @param view
     */
    public  void onVideoDelete(View view){
        //图片名称，测试请使用上面列表获取的文件名称 Image name, please use the file name obtained from the above list for testing
        String pictureName = "VID_20200531_160006_69.mp4";

        String url =  String.format(mVideoDelete,mIp,pictureName);
        Log.d(TAG,"onVideoDelete url:"+url);
        OkHttpClient client = new OkHttpClient();
        Request request = new Request.Builder().url(url).build();
        Call call = client.newCall(request);
        call.enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                //...
            }
            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if(response.isSuccessful()){

                    String result = response.body().string();
                    //处理UI需要切换到UI线程处理 Processing UI requires switching to UI thread processing
                    Log.d(TAG,"onVideoDelete result:"+result);
                }
            }
        });
    }

    /***
     * 图片下载 image download
     * @param view
     */
    public void onPictureDownload(View view){
        Log.d("TESTINGTAG","DownloadActivity,onPictureDownload method reached start");
        //图片名称，测试请使用上面列表获取的文件名称 Image name, please use the file name obtained from the above list for testing
        String pictureName = "IMG_20221107_101357_621.jpeg";

        String url =  String.format(mPictureDownload,mIp,pictureName);

        //创建下载任务,downloadUrl就是下载链接 Create a download task, downloadUrl is the download link
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名 Specify the download path and download file name
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_PICTURES, pictureName);
        //获取下载管理器 Get a download manager
        DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载 Add the download task to the download queue, otherwise it will not be downloaded
        downloadManager.enqueue(request);
        Log.d("TESTINGTAG","DownloadActivity,onPictureDownload method reached end");
    }

    /***
     * 视频下载 video download
     * @param view
     */
    public void onVideoDownload(View view){

        //图片名称，测试请使用上面列表获取的文件名称 Image name, please use the file name obtained from the above list for testing
        String voideName = "VID_20200601_000048_26.mp4";
        String url =  String.format(mVideoDownload,mIp,voideName);
        //创建下载任务,downloadUrl就是下载链接 Create a download task, downloadUrl is the download link
        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        //指定下载路径和下载文件名 Specify the download path and download file name
        request.setDestinationInExternalPublicDir("/Download/", voideName);
        //获取下载管理器 Get a download manager
        DownloadManager downloadManager= (DownloadManager) getSystemService(Context.DOWNLOAD_SERVICE);
        //将下载任务加入下载队列，否则不会进行下载 Add the download task to the download queue, otherwise it will not be downloaded
        downloadManager.enqueue(request);
    }
}

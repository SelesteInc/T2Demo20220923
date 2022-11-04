package com.weseeing.t2demo.ui;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.res.Configuration;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.constraintlayout.widget.ConstraintLayout;

import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.cmd.SeeT2CmdId;
import com.weseeing.t2demo.cmd.SeeT2PlusCmdId;
import com.weseeing.t2demo.cmd.SeeTCmdDataHelperManage;
import com.weseeing.t2demo.cmd.SeeTCmdHelper;
import com.weseeing.t2demo.databinding.ActivityLiveBinding;
import com.weseeing.t2demo.utils.Constant;
import com.weseeing.t2demo.utils.ScreenUtils;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.TimeZone;

import tv.danmaku.ijk.media.example.widget.media.IjkVideoView;


public class LiveActivity extends AppCompatActivity implements View.OnClickListener{
    private static final String TAG = "T2.VideoPlayActivity";

    boolean isRecording = false; //是否录像 Whether to record
    //录像开始时间 Recording start time
    Long startTime = System.currentTimeMillis();
    int  mStreamVolume = 5;

    SimpleDateFormat sdf = new SimpleDateFormat("H:mm:ss", Locale.getDefault());

    private ActivityLiveBinding binding;
    private SeeDeviceChannel mSeeDevice;
    public  static void lunchar(Context context){
        Intent intent = new Intent(context, LiveActivity.class);

        context.startActivity(intent);

    }

    private Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            super.handleMessage(msg);
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityLiveBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getSupportActionBar().hide();

        init();
    }
    private void init(){
        binding.tvPlayer.setOnClickListener(this);
        binding.tvScreenFull.setOnClickListener(this);
        binding.tvRecord.setOnClickListener(this);
        binding.tvScreenshot.setOnClickListener(this);

        binding.tvRecord.setText("录像");

        mSeeDevice = SeeDeviceChannel.create(getApplication(),SeeDeviceChannel.isBindType(getApplication()),null);
        sendStartLiveCmd();
    }

    private void  sendStartLiveCmd(){
        sendCmd(SeeTCmdDataHelperManage.getCmdDataStarLive(mSeeDevice.getType()));
    }

    private void  sendStopLiveCmd(){
        sendCmd(SeeTCmdDataHelperManage.getCmdDataStopLive(mSeeDevice.getType()));
    }

    /*** 返回当前屏幕是否为竖屏。 Returns whether the current screen is vertical.
      * @return 当且仅当当前屏幕为竖屏时返回true,否则返回false。Returns true if and only if the current screen is vertical, otherwise returns false.
     */
    public  boolean isScreenOriatationPortrait() {
        return getResources().getConfiguration().orientation == ActivityInfo.SCREEN_ORIENTATION_PORTRAIT;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK) && ScreenUtils.isLandscape(this)) {
            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
            return false;
        }else {
            return super.onKeyDown(keyCode, event);
        }
    }

    private void startLive() {
        if (!mSeeDevice.isConnect()){
            Toast.makeText(this, "TCP未连接设备", Toast.LENGTH_SHORT).show(); // TCP not connected device
        }
        String ip = mSeeDevice.getAddress();
        ip = TextUtils.isEmpty(ip) ? "192.168.0.1" : ip;


        String rtspUlr ="";
        if (mSeeDevice.getType() == SeeDeviceChannel.TYPE_T2_DEVICE){
            rtspUlr ="rtsp://"+ip+"/test.265";;
        }else {
            rtspUlr = String.format("rtsp://%s:8554/live",ip);
        }
        Log.d(TAG,"rtspUlr: "+rtspUlr);
        binding.videoView.setVideoPath(rtspUlr);
        binding.videoView.setRender(IjkVideoView.RENDER_TEXTURE_VIEW);
        binding.videoView.start();
    }

    private void stopLive() {
        binding.videoView.stopPlayback();
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG,"onPause");
    }

    @Override
    protected void onResume() {
        super.onResume();
        if (binding.videoView.isPlaying())startLive();
        Log.d(TAG,"onResume");
    }

    @Override
    protected void onStop() {
        super.onStop();
        if (isRecording)switchVideoRecord();
        Log.d(TAG,"onStop");
    }


    @Override
    protected void onDestroy() {
        Log.d(TAG,"onDestroy");
        stopLive();
        sendStopLiveCmd();
        super.onDestroy();
    }

    @Override
    public void onConfigurationChanged(@NonNull  Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        boolean isLandscape = (newConfig.orientation == Configuration.ORIENTATION_LANDSCAPE?true:false);
        int resourceId = getResources().getIdentifier("status_bar_height", "dimen", "android");
        int statusBarHeight = getResources().getDimensionPixelSize(resourceId);

        int width ;
        int height;
        if (isLandscape){
            width= ScreenUtils.getScreenWidth(this);
            height = ScreenUtils.getScreenHeight(this);
            getWindow().addFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
        }else {
            getWindow().clearFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN);
            width = ScreenUtils.getScreenWidth(this);
            height = (ScreenUtils.getScreenHeight(this))/19*6-statusBarHeight;
        }

        ConstraintLayout.LayoutParams layoutParams = new ConstraintLayout.LayoutParams(width,height);
        binding.videoParent.setLayoutParams(layoutParams);
    }

    public void onClick(View view) {
        int id = view.getId();
        if (!binding.videoView.isPlaying() && id != R.id.tv_player){
            Toast.makeText(this,"请先开启播放",Toast.LENGTH_SHORT).show(); // Please start playback first
            return;
        }
        switch (id){
            case R.id.tv_player:
                startLive();
                break;
            case R.id.tv_screen_full:
                if (isScreenOriatationPortrait())
                setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_LANDSCAPE);
                break;
            case R.id.tv_screenshot:
                boolean save = binding.videoView.snapshotPicture(getFolder().getAbsolutePath()+ File.separator,(new SimpleDateFormat("yyyyMMddHHmmss")).format(new Date()) + ".jpg");
                if (save)Toast.makeText(this, "截图成功保存", Toast.LENGTH_SHORT).show(); // Screenshot saved successfully
                break;
            case R.id.tv_record:
                if (isRecording){
                    stopRecord();
                }else {
                    startRecord(getFolder().getAbsolutePath()+ File.separator);
                }
                break;
        }
    }

    private File getFolder() {
        File rootFolder = new File(Constant.ROOT_PATH);
        if (!rootFolder.exists()) {
            rootFolder.mkdir();
        }
        return rootFolder;
    }

    private void startRecord(String filePath) {
        boolean isRecord = binding.videoView.startRecord(filePath, (new SimpleDateFormat("-yyyyMMddHHmmss")).format(new Date()) + ".mp4");
        if (isRecord) {
            Toast.makeText(this, "开始录像", Toast.LENGTH_SHORT).show(); // start recording
            switchVideoRecord();
        } else {
            Toast.makeText(this, "开始录像失败", Toast.LENGTH_SHORT).show(); // Failed to start recording
        }
    }

    private void stopRecord() {
        boolean isStop = binding.videoView.stopRecord();
        if (isStop) {
            switchVideoRecord();
            Toast.makeText(this, "停止录像", Toast.LENGTH_SHORT).show(); // stop recording
        } else {
            Toast.makeText(this, "停止录像失败", Toast.LENGTH_SHORT).show(); // Failed to stop recording
        }
    }


    private  void switchVideoRecord(){
        Log.e(TAG, "switchVideoRecord:" + isRecording);
        isRecording =  !isRecording;
        if (isRecording) {
            startTime = System.currentTimeMillis();
            //开启计时器 start timer
            mHandler.postDelayed(runnable, 1000);
        } else {
            //停止计时器 stop timer
            mHandler.removeCallbacks(runnable);
            binding.tvRecord.setText("录像");;
        }
    }

    private Runnable runnable = new Runnable() {
        @Override
        public void run() {
            Log.d("TAG", "run: ");
            Long time = System.currentTimeMillis() - startTime;
            sdf.setTimeZone(TimeZone.getTimeZone("GMT+0"));
            binding.tvRecord.setText(sdf.format(time));
            mHandler.postDelayed(this, 1000);
        }
    };

    private void sendCmd(int id ,String jsonDate){
        sendCmd(new SeeTCmdData(mSeeDevice.getType(),id,jsonDate));
    }
    private void sendCmd(SeeTCmdData cmdData){
        mSeeDevice.sendCmdData(cmdData);
    }
}
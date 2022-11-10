package com.weseeing.t2demo;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.SeeDeviceChannelCallback;
import com.weseeing.framework.SeeFtpServerManage;
import com.weseeing.framework.common.SeeTCommState;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.framework.t.SeeTDataInfo;
import com.weseeing.t2demo.cmd.SeeT2CmdId;
import com.weseeing.t2demo.cmd.SeeTCmdDataHelperManage;
import com.weseeing.t2demo.cmd.SeeT2PlusCmdId;
import com.weseeing.t2demo.cmd.SeeTCmdHelper;
import com.weseeing.t2demo.databinding.ActivityMainDemoBinding;
import com.weseeing.t2demo.ui.AlbumActivity;
import com.weseeing.t2demo.ui.DownloadActivity;
import com.weseeing.t2demo.ui.LiveActivity;
import com.weseeing.t2demo.ui.SettingActivity;
import com.weseeing.t2demo.ui.T2PlusPairNetworkActivity;
import com.weseeing.t2demo.utils.Constant;
import com.weseeing.t2demo.utils.Utils;

import java.io.File;

public class DemoMainActivity extends AppCompatActivity implements SeeDeviceChannelCallback, View.OnClickListener {

    private static final String TAG = "DemoMainActivity" ;
    private static final String BASETAG = "TST DemoMain";

    /**
     * 设备通信 Device communication
     */
    private SeeDeviceChannel mSeeDevice;

    /**
     * FTP服务器 FTP server
     */
    private SeeFtpServerManage mFtpManage;

    private ActivityMainDemoBinding binding;
    private int mAaudioType = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(BASETAG, "onCreate hit");
        super.onCreate(savedInstanceState);
        setupPermissions();

        binding = ActivityMainDemoBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        init();
    }

    private void init() {
        //设备通信管理 Device Communication Management
        initSeeDevice();

        //ftp服务管理 ftp service management
        mFtpManage = SeeFtpServerManage.create();
        startFtp();

        binding.btnT2PlusPairNetwork.setOnClickListener(this);
        binding.btnLive.setOnClickListener(this);

        binding.btnFtpStart.setOnClickListener(this);

        binding.btnFtpStop.setOnClickListener(this);
        binding.btnPhoto.setOnClickListener(this);
        binding.btnUnbind.setOnClickListener(this);

        binding.btnShutdown.setOnClickListener(this);
        binding.btnReboot.setOnClickListener(this);
    }

    private void initSeeDevice(){
        //设备通信管理 Device Communication Management
        int type = SeeDeviceChannel.isBindType(getApplication());
        if ( mSeeDevice == null ){
            Log.e(BASETAG,"SeeDeviceChannel Create Type:"+type);
            mSeeDevice = SeeDeviceChannel.create(getApplication(),type ,null);
            //OkSocketOptions.setIsDebug(true);
            //SLog.setIsDebug(true);
        }else {
            int oldType = mSeeDevice.getType();
            if (mSeeDevice !=null && type != oldType  ){
                SeeDeviceChannel.destroy(mSeeDevice.getType());
                mSeeDevice = SeeDeviceChannel.create(getApplication(),type ,null);
                Log.e(BASETAG,"SeeDeviceChannel ReCreate NewType:"+type +" oldType:"+oldType);
            }
        }
    }

    private void initData() {

    }

    @Override
    protected void onResume() {
        super.onResume();
        initSeeDevice();
        binding.tvLocalIp.setText("local IP:"+Utils.getLocalIp());
        binding.tvDeviceIp.setText("Device IP:"+mSeeDevice.getAddress());
        mSeeDevice.addChannelCallback(this);
        Log.e(TAG,"onResume()"+this);
    };

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"onStop()"+this);
        mSeeDevice.removeChannelCallback(this);
    }

    private void logRece(final SeeTCmdData cmdData) {
        if (Looper.myLooper() == Looper.getMainLooper()) {
            binding.tvDeviceInfo.setText(cmdData.toString());
            Log.d(TAG,"logRece:"+cmdData.toString());
        } else {
            final String threadName = Thread.currentThread().getName();
            new Handler(Looper.getMainLooper()).post(new Runnable() {
                @Override
                public void run() {
                    binding.tvDeviceInfo.setText(cmdData.toString());
                }
            });
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.menu_main, menu);
//
//        return true;
//    }

//    @Override
//    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.action_settings) {
//            if (isConnect()){
//                SettingActivity.launch(this);
//            }
//            return true;
//        }else if (id == R.id.action_download){
//            if (isConnect()){
//                DownloadActivity.launch(this);
//            }
//        }
//        else if (id == R.id.action_album){
//            AlbumActivity.launch(this);
//        }
//
//        return super.onOptionsItemSelected(item);
//    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSeeDevice.disconnect();
        mSeeDevice.removeChannelCallback(this);
        SeeDeviceChannel.destroy(mSeeDevice.getType());
    }

    @Override
    public void onReceive(Object data) {
        if ( null!=data && (data instanceof SeeTDataInfo)){
            SeeTDataInfo wiFiDataInfo = (SeeTDataInfo)data;
            handleData(wiFiDataInfo);
        }
    }
    public void handleData(SeeTDataInfo data) {
        int type = mSeeDevice.getType();
        switch (data.getState()) {
            case SeeTCommState.CONNECTION_SUCCESS:
                Toast.makeText(getApplication(),"Glasses connected successfully！",Toast.LENGTH_SHORT).show(); // Glasses connected successfully
                Log.e(TAG,"CONNECTION_SUCCESS");
                initDevices(type);
            break;
            case  SeeTCommState.DISCONNECTION:
                Log.e(TAG,"DISCONNECTION");
                Toast.makeText(getApplication(),"glasses disconnected！",Toast.LENGTH_SHORT).show(); // glasses disconnected
                break;
            case SeeTCommState.CONNECTION_FAILED:
                Log.e(TAG,"CONNECTION_FAILED");
                Toast.makeText(getApplication(),"glasses connection failed！",Toast.LENGTH_SHORT).show(); // glasses connection failed
                break;
            case SeeTCommState.RECEIVE_SUCCESS:
                SeeTCmdData cmdData = data.getData();
                logRece(cmdData);
                if (cmdData.getCommandID() == SeeT2CmdId.kMsgRestoreFactory
                  || cmdData.getCommandID() == SeeT2PlusCmdId.kMsgRestoreFactory){
                    Toast.makeText(getApplication(),"Factory reset was successful！",Toast.LENGTH_SHORT).show(); // Factory reset was successful
                    mSeeDevice.unbind();
                }
                break;
             case SeeTCommState.UNBIND:
                 Toast.makeText(getApplication(),"unBound！",Toast.LENGTH_SHORT).show(); // 眼镜解绑成功
                 break;
            default:
                break;
        }
    }


    private void initDevices(final int type){
        Log.e(BASETAG,"MainActivity initDevices Hit");
        //连接需要同步时间 The connection needs to synchronize the time
        sendCmd(SeeTCmdDataHelperManage.getCmdDataTime(type));

        //不同用户需求，是否需要设置ftp图片视频上传 Different user needs, whether to set ftp image and video upload
        sendCmd(SeeTCmdDataHelperManage.getCmdDataFtp(type,Utils.getLocalIp(),2222, "admin","SeeU123456"));
        if (type == SeeDeviceChannel.TYPE_T2PLUS_DEVICE)sendCmd(SeeT2PlusCmdId.kMsgStartFtpPut, SeeTCmdHelper.setDefaultJson(""));

    }


    @Override
    public void onClick(View v) {
        binding.tvDeviceInfo.setText("");//清空日志 clear log
        int type = mSeeDevice.getType();
        switch (v.getId()){
            case R.id.btnT2PlusPairNetwork:
                T2PlusPairNetworkActivity.launch(this);
                break;
            case R.id.btnLive:
                if (!isConnect())return;
                LiveActivity.lunchar(this);
                break;
            case R.id.btnFtpStart:
                startFtp();
                break;
            case R.id.btnFtpStop:
                stopFtp();
                break;
            case R.id.btnPhoto:
               sendCmd(SeeTCmdDataHelperManage.getCmdDataShoot(type));
                break;
//            case R.id.btnStartVideo:
//                sendCmd(SeeTCmdDataHelperManage.getCmdDataStarVideo(type));
//                break;
//            case R.id.btnStopVideo:
//                sendCmd(SeeTCmdDataHelperManage.getCmdDataStopVideo(type));
//                break;
            case R.id.btnUnbind:
                mSeeDevice.unbind();
                break;
//            case R.id.btnStatus:
//                sendCmd(SeeTCmdDataHelperManage.getCmdDataDevStatus(type));
//                break;
//            case R.id.btnAudio:
//                mAaudioType = mAaudioType > 18 ? 1:mAaudioType;
//                sendCmd(SeeTCmdDataHelperManage.getCmdDataAudioPlay(type,mAaudioType)); //开机启动提示音
//                mAaudioType++;
//                break;
            case R.id.btnReboot:
                sendCmd(SeeTCmdDataHelperManage.getCmdDataDevReboot(type));
                break;
            case R.id.btnShutdown:
                sendCmd(SeeTCmdDataHelperManage.getCmdDataDevShutdown(type));
                break;
//            case R.id.btnAlbum:
//                AlbumActivity.launch(this);
//                break;
        }
    }


    private boolean isConnect(){
        if (mSeeDevice ==null || !mSeeDevice.isConnect()){
            Toast.makeText(getApplication(),"Glasses not connected！",Toast.LENGTH_SHORT).show();
            return false;
        }
        return true;
    }

    private void setupPermissions() {
        int  permission = ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE);

        if (permission != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,
                            Manifest.permission.READ_EXTERNAL_STORAGE,
                            Manifest.permission.ACCESS_FINE_LOCATION,   //定位权限
                            Manifest.permission.RECORD_AUDIO},
                    111);
        }
    }

    /**
     * 启动ftp服务器 start ftp server
     */
    private void startFtp(){
        File file =new File(Constant.ROOT_PATH);
        if (!file.exists()){
            file.mkdirs();
        }
        try {
            mFtpManage.startFtp(Constant.ROOT_PATH,"admin","SeeU123456",2222);
            Log.d(BASETAG, "Ftp server started successfully");
            Toast.makeText(getApplication(),"Ftp server started successfully！",Toast.LENGTH_SHORT).show(); // Ftp server started successfully
            binding.tvFtpInfo.setText(Utils.getFtpUrlInfo());
            binding.tvFtpInfo.setVisibility(View.VISIBLE);
        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(getApplication(),"Ftp server failed to start！",Toast.LENGTH_SHORT).show(); // Ftp server failed to start
            Log.d(BASETAG, "Ftp server failed to start");

        }
    }

    /**
     * 停止Ftp服务器
     */
    private void stopFtp(){
        binding.tvFtpInfo.setText("");
        binding.tvFtpInfo.setVisibility(View.GONE);
        mFtpManage.stopFtp();
        Toast.makeText(getApplication(),"Ftp server stopped！",Toast.LENGTH_SHORT).show();
    }

    private void sendCmd(SeeTCmdData cmdData){
        if (!isConnect())return;
        mSeeDevice.sendCmdData(cmdData);
        Log.d(BASETAG + "sendCmd(See",cmdData.toString());
    }

    private void sendCmd(int id,String data){
        sendCmd(new SeeTCmdData(mSeeDevice.getType(),id,data));
        Log.d(BASETAG + "sendCmd(int",data);
    }
}


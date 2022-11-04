package com.weseeing.t2demo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.SeeDeviceChannelCallback;
import com.weseeing.framework.SeeSinVoiceManage;
import com.weseeing.framework.common.SeeSettings;
import com.weseeing.framework.common.SeeTCommState;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.framework.t.SeeTDataInfo;
import com.weseeing.framework.t.SeeTDevice;
import com.weseeing.framework.t.SeeTDeviceDiscover;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.cmd.SeeTCmdHelper;
import com.weseeing.t2demo.cmd.SeeT2CmdId;
import com.weseeing.t2demo.databinding.ActivityT2PairNetworkBinding;
import com.weseeing.t2demo.ui.adapter.DeviceAdapter;
import com.weseeing.t2demo.utils.Utils;

public class T2PairNetworkActivity extends AppCompatActivity implements  SeeTDeviceDiscover.OnDeviceDiscoverListener
        , View.OnClickListener , SeeDeviceChannelCallback {

    private static final String TAG = "T2PairNetworkActivity";

    private ActivityT2PairNetworkBinding binding;
    private DeviceAdapter mDeviceAdapter;

    private boolean isConnecting = false;

    /**
     * 设备搜索 device search
     */
    private SeeTDeviceDiscover mWiFiDiscover;
    private SeeTDevice mWiFiDevice;


    /**
     * 声波通信管理 Sonic Communication Management
     */
    private SeeSinVoiceManage mVoiceManage;

    /**
     * 设备通信
     */
    private SeeDeviceChannel mSeeDevice;

    public static void launch(Context context){
        Intent intent = new Intent(context, T2PairNetworkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityT2PairNetworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();

        binding.etIp.setText(SeeSettings.getLockedIp(this));
        String connect = mSeeDevice.isConnect() ? "断开" : "连接"; // disconnect : connect
        binding.btConnect.setText(connect);
    }


    private void init(){
        binding.btConnect.setOnClickListener(this);
        binding.btnSendSinVoice.setOnClickListener(this);
        binding.btnListenSinVoice.setOnClickListener(this);
        binding.btnRefresh.setOnClickListener(this);
        binding.btnStart.setOnClickListener(this);
        binding.btnStop.setOnClickListener(this);

        //配网 distribution network
        mVoiceManage = SeeSinVoiceManage.create(getApplication());
        mVoiceManage.setListener(mSeeListener);
        //mVoiceManage.enableWritePcmToFile(false);
        //mVoiceManage.startListen(false);
        mVoiceManage.stopListen();

        //设备扫描 Device scan
        mWiFiDiscover = SeeTDeviceDiscover.create();
        mWiFiDiscover.setOnDeviceDiscoverListener(this);

        //扫描设备列表适配器 scan device list adapter
        mDeviceAdapter = new DeviceAdapter(this);
        binding.listDevice.setAdapter(mDeviceAdapter);
        mDeviceAdapter.setOnConnectClickListener(mConnectClickListener);

        //设备通信管理 Device Communication Management
        mSeeDevice = SeeDeviceChannel.create(getApplication(), SeeDeviceChannel.TYPE_T2_DEVICE,null);
        mSeeDevice.addChannelCallback(this);
    }

    private DeviceAdapter.OnConnectClickListener mConnectClickListener = new DeviceAdapter.OnConnectClickListener() {
        @Override
        public void onClick(Object device) {
            if (device instanceof SeeTDevice){
                mWiFiDevice = (SeeTDevice)device;
                binding.etIp.setText(mWiFiDevice.getIp());
                connectDevice();
            }
        }
    };

    /**
     * 连接眼镜设备 Connect glasses device
     */
    private void connectDevice(){
        connect( binding.etIp.getText().toString());
    }

    private void connect(String ip){
        if (!mSeeDevice.isConnect() ) {
            if (isConnecting){
                Log.w(TAG,"isConnecting");
                return;
            }
            mSeeDevice.connect(ip);
            isConnecting = true;
            binding.etIp.setEnabled(false);
        } else {
            binding.btConnect.setText("Disconnecting");
            mSeeDevice.disconnect();
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mWiFiDiscover.start();
    }

    @Override
    protected void onStop() {
        super.onStop();
        mWiFiDiscover.stop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mSeeDevice.removeChannelCallback(this);
    }

    @Override
    public void onClick(View view) {
        binding.tvDeviceInfo.setText("");//清空日志
        switch (view.getId()){
            case R.id.btConnect:
                connectDevice();
                break;
            case R.id.btnSendSinVoice:
                if (mVoiceManage.isSending()) {
                    mVoiceManage.stopSend();
                } else {
                    String ssid = binding.etSsid.getText().toString();
                    String pwd = binding.etPwd.getText().toString();
                    if (TextUtils.isEmpty(ssid)){
                        Toast.makeText(this, "SSid不能为空！", Toast.LENGTH_SHORT).show(); // SSid cannot be empty!
                        return;
                    }
                    mVoiceManage.send(SeeTCmdHelper.setWiFiJsonT2(ssid,pwd));
                }
                break;
            case R.id.btnListenSinVoice:
                if (mVoiceManage.isListening()) {
                    mVoiceManage.stopListen();
                } else {
                    mVoiceManage.startListen(false);
                }
                break;
            case R.id.btnStart:
                mWiFiDiscover.start();
                break;
            case R.id.btnStop:
                mWiFiDiscover.stop();
                break;
            case R.id.btnRefresh:
                mWiFiDiscover.refresh();
                mDeviceAdapter.updateWiFiDeviceData(mWiFiDiscover.getDeviceList());
                mDeviceAdapter.notifyDataSetChanged();
                break;
        }
    }

    @Override
    public void onReceive(Object data) {
        if ( null!=data && (data instanceof SeeTDataInfo)){
            SeeTDataInfo wiFiDataInfo = (SeeTDataInfo)data;
            handleData(wiFiDataInfo);
        }
    }
    public void handleData(SeeTDataInfo data) {
        switch (data.getState()) {
            case SeeTCommState.CONNECTION_SUCCESS:
                Toast.makeText(getApplication(),"眼镜连接成功！",Toast.LENGTH_SHORT).show(); // Glasses connected successfully
                binding.btConnect.setText("断开"); // disconnect
                binding.etIp.setEnabled(false);
                isConnecting = false;
                initDevices();
                break;
            case  SeeTCommState.DISCONNECTION:
                Toast.makeText(getApplication(),"眼镜断开连接！",Toast.LENGTH_SHORT).show(); // glasses disconnected
                binding.btConnect.setText("连接"); // connect
                binding.etIp.setEnabled(true);
                isConnecting = false;
                break;
            case SeeTCommState.CONNECTION_FAILED:
                Log.e(TAG,"CONNECTION_FAILED");
                Toast.makeText(getApplication(),"眼镜连接失败！",Toast.LENGTH_SHORT).show(); // glasses connection failed
                binding.btConnect.setText("连接"); // connect
                binding.etIp.setEnabled(true);
                isConnecting = false;
                break;
            case SeeTCommState.RECEIVE_SUCCESS:
                SeeTCmdData cmdData = data.getData();
                binding.tvDeviceInfo.setText(cmdData.toString());
                Log.d(TAG,"RECEIVE_SUCCESS:"+cmdData.toString());
                if (SeeT2CmdId.kMsgDevStatus == cmdData.getCommandID()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            T2PairNetworkActivity.this.finish();
                        }
                    },2000);
                }
                break;
            default:
                break;
        }
    }
    @Override
    public void OnUpdatedDevice(SeeTDevice seeTDevice) {
        mDeviceAdapter.updateWiFiDeviceData(mWiFiDiscover.getDeviceList());
        mDeviceAdapter.notifyDataSetChanged();
    }


    private void  initDevices(){
        //同步时间 synchronised time
        sendCmd(SeeT2CmdId.kMsgSetDevTime, SeeTCmdHelper.setTimeJson(System.currentTimeMillis()));

        //根据用户需求来设置是否启动ftp照片上传 默认不设置 According to user needs to set whether to start ftp photo uploading is not set by default
        sendCmd(SeeT2CmdId.kMsgSetFtpParameters, SeeTCmdHelper.setFtpJson(Utils.getLocalIp(),2222, "admin","SeeU123456"));

        //获取设备状态 Get device status
        sendCmd(SeeT2CmdId.kMsgDevStatus, SeeTCmdHelper.setDefaultJson(""));

    }


    /**
     * 声波通信 状态回调 Sonic communication Status callback
     */
    private SeeSinVoiceManage.SeeListener mSeeListener = new SeeSinVoiceManage.SeeListener() {
        @Override
        public void onSinVoiceSendStart() {
            binding.btnSendSinVoice.setText(R.string.stop_send);
        }

        @Override
        public void onSinVoiceSendFinish() {
            binding.btnSendSinVoice.setText(R.string.send_data);
        }

        @Override
        public void onSinVoiceStartListen() {
            setState(getString(R.string.state_listening_sonic));
            binding.btnListenSinVoice.setText(R.string.stop_listen);
        }

        @Override
        public void onSinVoiceStopListen() {
            setState(getString(R.string.state_stop_listening_sonic));
            binding.btnListenSinVoice.setText(R.string.start_listen);
        }

        @Override
        public void onSinVoiceReceiveStart() {
            setState(getString(R.string.state_receiving_data));
        }

        @Override
        public void onSinVoiceReceiveSuccess(String var1) {
            setState( getString(R.string.state_receiving_successful),var1);
        }

        @Override
        public void onSinVoiceReceiveFailed() {
            setState(getString(R.string.state_listening_sonic), getString(R.string.state_receiving_failed));
        }
        //日志显示 log display
        private void setState(String str1) {
            setState(str1, "");
        }

        private void setState(String str1, String str2) {
            String stateString = getString(R.string.state);
           binding.tvSinVoiceInfo.setText(String.format(stateString, str1, str2));
        }
    };

    private SeeTCmdData getCmdData(int cmdId,String data){
        SeeTCmdData cmdData = new SeeTCmdData(mSeeDevice.getType(),cmdId,data);
        return cmdData;
    }

    private void sendCmd(int id,String data){
        if (!mSeeDevice.isConnect())return;
        mSeeDevice.sendCmdData(id, data);
    }

}
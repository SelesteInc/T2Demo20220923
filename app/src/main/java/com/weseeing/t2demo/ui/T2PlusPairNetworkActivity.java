package com.weseeing.t2demo.ui;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import android.app.AlertDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Toast;

import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.SeeDeviceChannelCallback;
import com.weseeing.framework.common.SeeSettings;
import com.weseeing.framework.common.SeeTCommState;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.framework.t.SeeTDataInfo;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.cmd.SeeT2PlusCmdId;
import com.weseeing.t2demo.cmd.SeeTCmdDataHelperManage;
import com.weseeing.t2demo.cmd.SeeTCmdHelper;
import com.weseeing.t2demo.databinding.ActivityT2PlusPairNetworkBinding;
import com.weseeing.t2demo.ui.adapter.BleRssiDevice;
import com.weseeing.t2demo.ui.adapter.MyBleWrapperCallback;
import com.weseeing.t2demo.ui.adapter.ScanAdapter;
import org.json.JSONException;
import org.json.JSONObject;
import java.nio.charset.Charset;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleStatusCallback;
import cn.com.heaton.blelibrary.ble.callback.BleWriteCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.BleFactory;
import cn.com.heaton.blelibrary.ble.model.ScanRecord;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;
import cn.com.heaton.blelibrary.ble.utils.Utils;

public class T2PlusPairNetworkActivity extends AppCompatActivity implements View.OnClickListener, SeeDeviceChannelCallback {
    private static final String TAG = "T2PlusPairNetworkActivity";
    private static final String BASETAG = "TST T2Plus";

    public static final int REQUEST_GPS = 4001;

    private ActivityT2PlusPairNetworkBinding binding;

    private ScanAdapter adapter;

    private List<BleRssiDevice> bleRssiDevices;
    protected Ble<BleRssiDevice> ble = Ble.getInstance();
    protected Ble<BleDevice>  mConnectBle = Ble.getInstance();

    private BleDevice mBleDevice;

    private  List<BleDevice> mBleDeviceConnectList = new ArrayList<BleDevice>();

    private List<BluetoothGattService> mListBluetoothGattService;

    private UUID mCharacteristicUuid  ;
    private UUID mServiceUuid ;
    private ActionBar actionBar;

    public UUID read_service_uuid = UUID.fromString("02f00000-0000-0000-0000-00000000fe00");
    public UUID read_characerisric_uuid = UUID.fromString("02f00000-0000-0000-0000-00000000ff02");

    private int MTU =128;  //蓝牙默认数据包最大长度

    /**
     * 设备通信 Device communication
     */
    private SeeDeviceChannel mSeeDevice;

    public static void launch(Context context){
        Log.d(BASETAG, "Launch hit");
        Intent intent = new Intent(context, T2PlusPairNetworkActivity.class);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(intent);
    }

    public static final int MSG_ENABLENOTIFY = 1;
    protected Handler mHandler = new Handler(Looper.getMainLooper()){
        @Override
        public void handleMessage(@NonNull Message msg) {
            switch (msg.what){
                case MSG_ENABLENOTIFY:
                    setEnableNotify(true);
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(BASETAG, "onCreate hit");
        super.onCreate(savedInstanceState);
        binding = ActivityT2PlusPairNetworkBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        init();
    }
    protected void init(){
        actionBar = getSupportActionBar();
        initBle();
        initAdapter();
        initLinsenter();
        initBleStatus();
        checkBlueStatus();


        binding.etIp.setText(SeeSettings.getLockedIp(this));

        binding.etSsid.setText(SeeSettings.getLockedSsid(this));
        binding.etPwd.setText(SeeSettings.getLockedPwd(this));

        mSeeDevice = SeeDeviceChannel.create(getApplication(),SeeDeviceChannel.TYPE_T2PLUS_DEVICE,null);
        mSeeDevice.addChannelCallback(this);

        String mac = SeeSettings.getLockedAddress(this);
        if ( !TextUtils.isEmpty(mac) ){
            mConnectBle.connect(mac,connectCallback);
        }

        String connect = mSeeDevice.isConnect() ? "断开" : "连接"; // disconnect : connect
        binding.btIpConnect.setText(connect);
    }

    private void initBle() {
        Ble.options()
                .setLogBleEnable(false)//设置是否输出打印蓝牙日志 Set whether to output and print bluetooth logs
                .setThrowBleException(true)//设置是否抛出蓝牙异常 Set whether to throw bluetooth exception
                .setLogTAG("AndroidBLE")//设置全局蓝牙操作日志TAG Set the global Bluetooth operation log TAG
                .setAutoConnect(false)//设置是否自动连接 Set whether to connect automatically
                .setIgnoreRepeat(false)//设置是否过滤扫描到的设备(已扫描到的不会再次扫描) Set whether to filter the scanned devices (the scanned devices will not be scanned again)
                .setConnectFailedRetryCount(3)//连接异常时（如蓝牙协议栈错误）,重新连接次数 When the connection is abnormal (such as the Bluetooth protocol stack error), the number of reconnections
                .setConnectTimeout(10 * 1000)//设置连接超时时长 Set the connection timeout period
                .setScanPeriod(12 * 1000)//设置扫描时长 Set scan duration
                .setMaxConnectNum(10)//最大连接数量 Maximum number of connections
                .setUuidService(read_service_uuid)//设置主服务的uuid Set the uuid of the main service
                .setUuidWriteCha(UUID.fromString("02f00000-0000-0000-0000-00000000ff01"))//设置可写特征的uuid Set the uuid of the writable characteristic
                .setUuidReadCha(read_characerisric_uuid)//设置可读特征的uuid （选填）Set the uuid of the readable characteristic (optional)
//                .setUuidNotifyCha(read_characerisric_uuid)//设置可通知特征的uuid （选填，库中默认已匹配可通知特征的uuid）Set the uuid of the notifiable feature (optional, the uuid of the notifiable feature has been matched by default in the library)
                .setFactory(new BleFactory<BleRssiDevice>() {//实现自定义BleDevice时必须设置 Must be set when implementing custom BleDevice
                    @Override
                    public BleRssiDevice create(String address, String name) {
                        return new BleRssiDevice(address, name);//自定义BleDevice的子类 Custom subclasses of BleDevice
                    }
                })
                .setBleWrapperCallback(new MyBleWrapperCallback())
                .create(getApplication(), new Ble.InitCallback() {
                    @Override
                    public void success() {
                        BleLog.e("MainApplication", "Initialization successful");
                    } // Initialization successful

                    @Override
                    public void failed(int failedCode) {
                        BleLog.e("MainApplication", "initialization failed：" + failedCode);
                    } // initialization failed
                });
    }


    /**
     * 连接眼镜设备 Connect glasses device
     */
    private void connectDevice(){
        connect( binding.etIp.getText().toString());
    }

    private void connect(String ip){
        if (!mSeeDevice.isConnect() ) {
            mSeeDevice.connect(ip);
            binding.btIpConnect.setEnabled(false);
        } else {
            binding.btIpConnect.setText("Disconnecting");
            mSeeDevice.disconnect();
            binding.btIpConnect.setEnabled(false);
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
        int type = SeeDeviceChannel.TYPE_T2PLUS_DEVICE;
        switch (data.getState()) {
            case SeeTCommState.CONNECTION_SUCCESS:
                Toast.makeText(getApplication(),"Glasses connected successfully！",Toast.LENGTH_SHORT).show(); // Glasses connected successfully
                binding.btIpConnect.setText("disconnect"); // disconnect
                binding.btIpConnect.setEnabled(true);
                initDevices();
                break;
            case  SeeTCommState.DISCONNECTION:
                Toast.makeText(getApplication(),"glasses disconnected！",Toast.LENGTH_SHORT).show(); // glasses disconnected
                binding.btIpConnect.setText("connect"); // connect
                binding.btIpConnect.setEnabled(true);
                break;
            case SeeTCommState.CONNECTION_FAILED: ;
                Log.e(TAG,"CONNECTION_FAILED");
                Toast.makeText(getApplication(),"Glasses connection failed!",Toast.LENGTH_SHORT).show(); // Glasses connection failed!
                binding.btIpConnect.setText("connect"); // connect
                binding.btIpConnect.setEnabled(true);
                break;
            case SeeTCommState.RECEIVE_SUCCESS:

                SeeTCmdData cmdData = data.getData();
                binding.tvDeviceInfo.setText(cmdData.toString());
                Log.d(TAG,"RECEIVE_SUCCESS:"+cmdData.toString());
                if (SeeT2PlusCmdId.kMsgDevStatus == cmdData.getCommandID()){
                    new Handler().postDelayed(new Runnable() {
                        @Override
                        public void run() {
                            T2PlusPairNetworkActivity.this.finish();
                        }
                    },2000);
                }
                break;
            default:
                break;
        }
    }

    private void initDevices(){
        //连接需要同步时间 The connection needs to synchronize the time
        sendCmd(SeeT2PlusCmdId.kMsgSetDevTime, SeeTCmdHelper.setTimeJson(System.currentTimeMillis()));

        //不同用户需求，是否需要设置ftp图片视频上传 Different user needs, whether to set ftp image and video upload
        sendCmd(SeeT2PlusCmdId.kMsgSetFtpParameters,SeeTCmdHelper.setFtpJson(com.weseeing.t2demo.utils.Utils.getLocalIp(),2222, "admin","SeeU123456"));
        sendCmd(SeeT2PlusCmdId.kMsgStartFtpPut,SeeTCmdHelper.setDefaultJson(""));
        Log.d(BASETAG + "LOC_IP","LOCAL IP: " +com.weseeing.t2demo.utils.Utils.getLocalIp());

        //设备状态 device status
        sendCmd(SeeT2PlusCmdId.kMsgDevStatus,SeeTCmdHelper.setDefaultJson(""));
    }

    @Override
    public void onClick(View v) {
        if ( mBleDevice == null || (!mBleDevice.isConnected())){
            toast("Bluetooth device not connected！"); // Bluetooth device not connected
            return;
        }
        binding.tvDeviceInfo.setText("");//清空日志 clear log
        switch (v.getId()){
            case R.id.btConfigNetwork:
                String ssid = binding.etSsid.getText().toString();
                String pwd = binding.etPwd.getText().toString();
                if (TextUtils.isEmpty(ssid)){
                    Toast.makeText(this, "SSid cannot be empty!！", Toast.LENGTH_SHORT).show(); // SSid cannot be empty!
                    return;
                }

                SeeSettings.setLockedSsid(this,ssid);
                SeeSettings.setLockedPwd(this,pwd);

                String jsonData = SeeTCmdHelper.setWiFiJsonT2Plus(ssid,pwd);
                SeeTCmdData cmdData = getCmdData(SeeT2PlusCmdId.kMsgGetWifiConnect,jsonData);
                writeChar(mBleDevice,cmdData.parseBle(),mServiceUuid,mCharacteristicUuid);
                break;
            case R.id.btBleGetIp:
                jsonData = SeeTCmdHelper.setDefaultJson("");
                cmdData =  getCmdData(SeeT2PlusCmdId.kMsgBtGetDevIP,jsonData);
                writeChar(mBleDevice,cmdData.parseBle(),mServiceUuid,mCharacteristicUuid);
                break;
            case R.id.btIpConnect:
                connectDevice();
                break;

//            case R.id.btBleUvcMode:
//                jsonData = SeeTCmdHelper.setDefaultJson("");
//                cmdData =  getCmdData(SeeTCmdConstant.kMsgBtSwitchUvcOn,jsonData);
//                writeChar(mBleDevice,cmdData.parse(),mServiceUuid,mCharacteristicUuid);
//                break;
//            case R.id.btBleDeviceMode:
//                jsonData = SeeTCmdHelper.setDefaultJson("");
//                cmdData =  getCmdData(SeeTCmdConstant.kMsgBtSwitchUvcOff,jsonData);
//                writeChar(mBleDevice,cmdData.parse(),mServiceUuid,mCharacteristicUuid);
//                break;

        }
    }

    private void initAdapter() {
        bleRssiDevices = new ArrayList<>();
        adapter = new ScanAdapter(this, bleRssiDevices);
        binding.recyclerView .setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
        binding.recyclerView.getItemAnimator().setChangeDuration(300);
        binding.recyclerView.getItemAnimator().setMoveDuration(300);
        binding.recyclerView.setAdapter(adapter);
    }
    private void initLinsenter() {
        binding.btConfigNetwork.setOnClickListener(this);
        binding.btBleGetIp.setOnClickListener(this);
//        binding.btBleDeviceMode.setOnClickListener(this);
//        binding.btBleUvcMode.setOnClickListener(this);
        binding.btIpConnect.setOnClickListener(this);

        binding.tvAdapterStates.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent enableBtIntent = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
                startActivityForResult(enableBtIntent, Ble.REQUEST_ENABLE_BT);
            }
        });

        binding.swipeLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                binding.swipeLayout.setRefreshing(false);
                rescan();
            }
        });
        adapter.setConnectListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Ble<BleDevice> ble = Ble.getInstance();
                if (ble.isScanning()) {
                    ble.stopScan();
                }

                BleRssiDevice device = (BleRssiDevice)v.getTag();
                if (mBleDevice != null && device.isConnected()) {
                    mConnectBle.enableNotifyByUuid(device,false,mServiceUuid,mCharacteristicUuid,null);
                    mConnectBle.enableNotifyByUuid(device,false,read_service_uuid,read_characerisric_uuid,null);
                    mConnectBle.disconnect(device);
                    Log.e(TAG,"disconnect device:"+device.getBleAddress() +" isConnected:"+device.isConnected() );
                }else {
                    Log.e(TAG,"device:"+device.getBleAddress() +" isConnected:"+device.isConnected() );

                    mConnectBle.connect(device,connectCallback);
                }
            }
        });
    }


    private void rescan() {
        if (ble != null && !ble.isScanning()) {
            bleRssiDevices.clear();
            adapter.notifyDataSetChanged();
            ble.startScan(scanCallback);
        }
    }


    public BleScanCallback<BleRssiDevice> scanCallback = new BleScanCallback<BleRssiDevice>() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onLeScan(final BleRssiDevice device, int rssi, byte[] scanRecord) {
            synchronized (ble.getLocker()) {
                if (TextUtils.isEmpty(device.getBleName()))return;
                for (int i = 0; i < bleRssiDevices.size(); i++) {
                    BleRssiDevice rssiDevice = bleRssiDevices.get(i);
                    if (TextUtils.equals(rssiDevice.getBleAddress(), device.getBleAddress())){
                        if (rssiDevice.getRssi() != rssi && System.currentTimeMillis()-rssiDevice.getRssiUpdateTime() >1000L){
                            rssiDevice.setRssiUpdateTime(System.currentTimeMillis());
                            rssiDevice.setRssi(rssi);
                            adapter.notifyItemChanged(i);
                        }
                        return;
                    }
                }

                device.setScanRecord(ScanRecord.parseFromBytes(scanRecord));
                device.setRssi(rssi);
                bleRssiDevices.add(device);
                adapter.notifyDataSetChanged();
            }
        }

        @Override
        public void onStart() {
            super.onStart();
        }

        @Override
        public void onStop() {
            super.onStop();
        }

        @Override
        public void onScanFailed(int errorCode) {
            super.onScanFailed(errorCode);
            Log.e(TAG, "onScanFailed: "+errorCode);
        }
    };

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            //Log.e(TAG, "onConnectionChanged: " + device.getConnectionState()+Thread.currentThread().getName());
            if (device.isConnected()) {
                actionBar.setTitle(device.getBleAddress());
                actionBar.setSubtitle("connected"); // connected
                mBleDeviceConnectList.add(device);
                mBleDevice = device;
                SeeSettings.setLockedAddress(getApplication(),device.getBleAddress());
                Toast.makeText(getApplication(),"bluetooth connected",Toast.LENGTH_SHORT).show(); // bluetooth connected
            }else if (device.isConnecting()){
                actionBar.setTitle(device.getBleAddress());
                actionBar.setSubtitle("connecting..."); // connecting...
            }
            else if (device.isDisconnected()){
                //mBleDevice = null;
                mBleDeviceConnectList.remove(device);
                actionBar.setTitle("T2Plus");
                actionBar.setSubtitle("not connected"); // not connected
                Toast.makeText(getApplication(),"bluetooth disconnected",Toast.LENGTH_SHORT).show(); // bluetooth disconnected
            }
        }

        @Override
        public void onConnectFailed(BleDevice device, int errorCode) {
            super.onConnectFailed(device, errorCode);
            //Utils.showToast("连接异常，异常状态码:" + errorCode); Connection exception, exception status code
        }

        @Override
        public void onConnectCancel(BleDevice device) {
            super.onConnectCancel(device);
            Log.e(TAG, "onConnectCancel: " + device.getBleName());
        }

        @Override
        public void onServicesDiscovered(BleDevice device, BluetoothGatt gatt) {
            super.onServicesDiscovered(device, gatt);
            //adapter.notifyDataSetChanged();

            mListBluetoothGattService = gatt.getServices();
            //Log.e(TAG, "mListBluetoothGattService size: " + mListBluetoothGattService.size() );
            for (int i=0;i<mListBluetoothGattService.size();i++){
                BluetoothGattService gattService = mListBluetoothGattService.get(i);
                List<BluetoothGattCharacteristic> listGattCharacteristic = gattService.getCharacteristics();
                //Log.e(TAG, "ListGattCharacteristic size: " + listGattCharacteristic.size() + "  " +gattService.getUuid());
                for (int j=0;j<listGattCharacteristic.size();j++){
                    BluetoothGattCharacteristic characteristic= listGattCharacteristic.get(j);
                    int charaProp = characteristic.getProperties();
                    if( (charaProp & BluetoothGattCharacteristic.PROPERTY_WRITE) != 0){
                        mCharacteristicUuid = characteristic.getUuid() ;
                        mServiceUuid =characteristic.getService().getUuid() ;
                        //Log.e(TAG, "characteristicUuid: " + mCharacteristicUuid + "serviceicUuid "+ mServiceUuid +" type:WRITE");
                    }
                }
            }

            if ( mBleDevice != null && mBleDevice.isConnected()){
                gatt.requestMtu(MTU);
                //SeeTCmdData cmd = new SeeTCmdData(SeeTCmdData.CMD_TIEM_SET,SeeTCmdManage.getTime());
                //writeChar(mBleDevice,cmd.parse(),mServiceUuid,mCharacteristicUuid);
                //Log.e(TAG," vlaue:"+ ByteUtils.toHexString(cmd.parse()));
            }

        }

        @Override
        public void onReady(final BleDevice device) {
            super.onReady(device);

            Log.e(TAG, "onReady device==:" + device.getBleAddress());
            //连接成功后，设置通知 After the connection is successful, set the notification
            mConnectBle.enableNotifyByUuid(device, true, mServiceUuid, mCharacteristicUuid, new BleNotifyCallback<BleDevice>() {
                @Override
                public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                    //Log.e(TAG, "write onChanged==data:" + ByteUtils.bytes2HexStr(characteristic.getValue()));
                }
            });

            mHandler.sendEmptyMessageDelayed(MSG_ENABLENOTIFY,1000);

        }
    };


    public void parseIp(SeeTCmdData cmdData){
        if ( cmdData!= null
                && (cmdData.getCommandID() == SeeT2PlusCmdId.kMsgBtGetDevIP
                ||cmdData.getCommandID() == SeeT2PlusCmdId.kMsgGetWifiConnect)){
            String strRece = new String(cmdData.getData(), Charset.forName("utf-8"));
            JSONObject jsonObject = null;
            try {
                jsonObject = new JSONObject(strRece);
                if (jsonObject.has("ip")){
                    String ip = jsonObject.getString("ip");
                    binding.etIp.setText(ip);
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onDestroy() {
        Log.e(TAG,"onDestroy");
        disconnectAll();
        mSeeDevice.removeChannelCallback(this);
        super.onDestroy();
    }

    protected void disconnectAll(){
        for (BleDevice device:mBleDeviceConnectList){
            mConnectBle.enableNotifyByUuid(device,false,mServiceUuid,mCharacteristicUuid,null);
            mConnectBle.enableNotifyByUuid(device,false,read_service_uuid,read_characerisric_uuid,null);
            if (device.isConnecting()){
                mConnectBle.cancelConnecting(device);
            }
            mConnectBle.disconnect(device);
        }
        mConnectBle.cancelCallback(connectCallback);
        mConnectBle.released();
    }

    protected void writeChar(BleDevice bleDevice, byte[] bytes, UUID serviceUuid, UUID characteristicUuid){
        Log.e(TAG, "writeChar==data:\n" + ByteUtils.bytes2HexStr(bytes) +" \nserviceUuid:" +serviceUuid +" characteristicUuid:"+characteristicUuid);
        Ble.getInstance().writeByUuid(
                bleDevice,
                bytes,
                serviceUuid,
                characteristicUuid,
                new BleWriteCallback<BleDevice>() {

                    @Override
                    public void onWriteSuccess(BleDevice device, BluetoothGattCharacteristic characteristic) {
                        toast("write feature success");
                    }

                    @Override
                    public void onWriteFailed(BleDevice device, int failedCode) {
                        super.onWriteFailed(device, failedCode);
                        toast("Failed to write feature:"+failedCode);
                    }
                });
    }

    void toast(String msg){
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplication(),"=="+msg,Toast.LENGTH_SHORT).show();
            }
        });
    }


    //监听蓝牙开关状态 Monitor the status of the bluetooth switch
    private void initBleStatus() {
        ble.setBleStatusCallback(new BleStatusCallback() {

            @Override
            public void onBluetoothStatusChanged(boolean isOn) {
                BleLog.i(TAG, "onBluetoothStatusOn: Is bluetooth turned on?>>>>:" + isOn);
                binding.llAdapterTip.setVisibility(isOn?View.GONE:View.VISIBLE);
                if (isOn){
                    checkGpsStatus();
                }else {
                    if (ble.isScanning()) {
                        ble.stopScan();
                    }
                }
            }
        });
    }

    //检查蓝牙是否支持及打开 Check if bluetooth is supported and turned on
    private void checkBlueStatus() {
        if (!ble.isSupportBle(this)) {
            finish();
        }
        if (!ble.isBleEnable()) {
           binding.llAdapterTip .setVisibility(View.VISIBLE);
        }else {
            checkGpsStatus();
        }
    }

    private void checkGpsStatus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !Utils.isGpsOpen(this)){

            new AlertDialog.Builder(this)
                    .setTitle("提示") // hint
                    .setMessage("为了更精确的扫描到Bluetooth LE设备,请打开GPS定位") // For more accurate scanning to Bluetooth LE devices, please turn on GPS positioning
                    .setPositiveButton("确定", (dialog, which) -> {
                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                        startActivityForResult(intent,REQUEST_GPS);
                    })
                    .setNegativeButton("取消", null) // Cancel
                    .create()
                    .show();
        }else {
            ble.startScan(scanCallback);
        }
    }

    public void setEnableNotify(boolean enable){
        if (mBleDevice ==null)return;
        mConnectBle.enableNotifyByUuid(mBleDevice,true,read_service_uuid,read_characerisric_uuid, new BleNotifyCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                BleLog.e(TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
                //Log.e(TAG, "onChanged==data:" + ByteUtils.bytes2HexStr(characteristic.getValue()));
                SeeTCmdData cmdData = new SeeTCmdData(mSeeDevice.getType(),characteristic.getValue());
                //mTvLog.setText(cmdData.toString() );
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        binding.tvDeviceInfo.setText(cmdData.toString());
                        parseIp(cmdData);
                    }
                });
            }
        });
    }

   private SeeTCmdData getCmdData(int cmdId,String data){
       SeeTCmdData cmdData = new SeeTCmdData(mSeeDevice.getType(),cmdId,data);
       Log.d(BASETAG + "getCmdData",cmdData.toString());
       return cmdData;
   }

    private void sendCmd(int id,String data){
        if (!mSeeDevice.isConnect())return;
        mSeeDevice.sendCmdData(id, data);
        Log.d(BASETAG + "sendCmd",data);
    }

}
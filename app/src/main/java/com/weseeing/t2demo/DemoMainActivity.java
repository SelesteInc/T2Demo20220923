package com.weseeing.t2demo;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.ActionBar;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.AlertDialog;
import android.bluetooth.BluetoothGatt;
import android.bluetooth.BluetoothGattCharacteristic;
import android.bluetooth.BluetoothGattService;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.provider.Settings;
import android.text.TextUtils;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.SeeDeviceChannelCallback;
import com.weseeing.framework.SeeFtpServerManage;
import com.weseeing.framework.common.SeeSettings;
import com.weseeing.framework.common.SeeTCommState;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.framework.t.SeeTDataInfo;
import com.weseeing.t2demo.cmd.SeeT2CmdId;
import com.weseeing.t2demo.cmd.SeeTCmdDataHelperManage;
import com.weseeing.t2demo.cmd.SeeT2PlusCmdId;
import com.weseeing.t2demo.cmd.SeeTCmdHelper;
import com.weseeing.t2demo.databinding.ActivityMainDemoBinding;
import com.weseeing.t2demo.databinding.ActivityT2PlusPairNetworkBinding;
import com.weseeing.t2demo.ui.AlbumActivity;
import com.weseeing.t2demo.ui.DownloadActivity;
import com.weseeing.t2demo.ui.LiveActivity;
import com.weseeing.t2demo.ui.SettingActivity;
import com.weseeing.t2demo.ui.T2PlusPairNetworkActivity;
import com.weseeing.t2demo.ui.adapter.BleRssiDevice;
import com.weseeing.t2demo.ui.adapter.MyBleWrapperCallback;
import com.weseeing.t2demo.ui.adapter.ScanAdapter;
import com.weseeing.t2demo.utils.Constant;
import com.weseeing.t2demo.utils.Utils;

import java.io.File;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.com.heaton.blelibrary.ble.Ble;
import cn.com.heaton.blelibrary.ble.BleLog;
import cn.com.heaton.blelibrary.ble.callback.BleConnectCallback;
import cn.com.heaton.blelibrary.ble.callback.BleNotifyCallback;
import cn.com.heaton.blelibrary.ble.callback.BleScanCallback;
import cn.com.heaton.blelibrary.ble.callback.BleStatusCallback;
import cn.com.heaton.blelibrary.ble.model.BleDevice;
import cn.com.heaton.blelibrary.ble.model.BleFactory;
import cn.com.heaton.blelibrary.ble.model.ScanRecord;
import cn.com.heaton.blelibrary.ble.utils.ByteUtils;

public class DemoMainActivity extends AppCompatActivity implements SeeDeviceChannelCallback, View.OnClickListener {
    /*
    * T2Plus BLE Variables Below
    * */
    private static final String T2TAG = "T2PlusPairMain";
    private static final String T2BASETAG2 = "TST T2PlusMain";

    public static final int REQUEST_GPS = 4001;

    private ActivityT2PlusPairNetworkBinding T2binding;

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

    private int MTU =128;  //Bluetooth default maximum packet length

    private String ip="192.168.43.28";

    /*
     * End T2Plus BLE Variables Below
     * */




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
                //T2PlusPairNetworkActivity.launch(this);
                connectDevice();
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
                //sendCmd(SeeTCmdDataHelperManage.getCmdDataDevShutdown(type));
                scan();
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

    /*
    * T2PairNetworkActivity Methods Below
    * */

    protected void T2init(){
        actionBar = getSupportActionBar();
        initBle();

        bleRssiDevices = new ArrayList<BleRssiDevice>();
        //initAdapter();
        //initLinsenter();
        initBleStatus();
        //checkBlueStatus();


//        T2binding.etIp.setText(SeeSettings.getLockedIp(this));
//
//        T2binding.etSsid.setText(SeeSettings.getLockedSsid(this));
//        T2binding.etPwd.setText(SeeSettings.getLockedPwd(this));

        mSeeDevice = SeeDeviceChannel.create(getApplication(),SeeDeviceChannel.TYPE_T2PLUS_DEVICE,null);
        mSeeDevice.addChannelCallback(this);

//        String mac = SeeSettings.getLockedAddress(this);
//        if ( !TextUtils.isEmpty(mac) ){
//            mConnectBle.connect(mac,connectCallback);
//        }
//
//        String connect = mSeeDevice.isConnect() ? "断开" : "连接"; // disconnect : connect
//        T2binding.btIpConnect.setText(connect);
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

    private void initBleStatus() {
        ble.setBleStatusCallback(new BleStatusCallback() {

            @Override
            public void onBluetoothStatusChanged(boolean isOn) {
                BleLog.i(T2TAG, "onBluetoothStatusOn: Is bluetooth turned on?>>>>:" + isOn);
                T2binding.llAdapterTip.setVisibility(isOn?View.GONE:View.VISIBLE);
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

    private void checkGpsStatus(){
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M
                && !cn.com.heaton.blelibrary.ble.utils.Utils.isGpsOpen(this)){
            Log.e(T2TAG, "Turn on the GPS service and permission for precising location");
//            new AlertDialog.Builder(this)
//                    .setTitle("hint") // hint
//                    .setMessage("For more accurate scanning to Bluetooth LE devices, please turn on GPS positioning") // For more accurate scanning to Bluetooth LE devices, please turn on GPS positioning
//                    .setPositiveButton("Ok", (dialog, which) -> {
//                        Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
//                        startActivityForResult(intent,REQUEST_GPS);
//                    })
//                    .setNegativeButton("Cancel", null) // Cancel
//                    .create()
//                    .show();
        }else {
            // ble.startScan(scanCallback);
            Log.i(T2TAG, "checkGpsStatus: gps permission is valid");
        }
    }

    public void scan() {
        bleRssiDevices.clear();
        ble.startScan(scanCallback);
    }

    public BleScanCallback<BleRssiDevice> scanCallback = new BleScanCallback<BleRssiDevice>() {
        @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
        @Override
        public void onLeScan(final BleRssiDevice device, int rssi, byte[] scanRecord) {
            synchronized (ble.getLocker()) {
                //if (android.text.TextUtils.isEmpty(device.getBleName()))return;
                for (int i = 0; i < bleRssiDevices.size(); i++) {
                    BleRssiDevice rssiDevice = bleRssiDevices.get(i);
                    if (TextUtils.equals(rssiDevice.getBleAddress(), device.getBleAddress())){
                        if (rssiDevice.getRssi() != rssi && System.currentTimeMillis()-rssiDevice.getRssiUpdateTime() >1000L){
                            rssiDevice.setRssiUpdateTime(System.currentTimeMillis());
                            rssiDevice.setRssi(rssi);
                            //adapter.notifyItemChanged(i);
                        }
                        return;
                    }
                }

                device.setScanRecord(ScanRecord.parseFromBytes(scanRecord));
                device.setRssi(rssi);
                bleRssiDevices.add(device);
                //adapter.notifyDataSetChanged();

                if (device.getBleName() != null && isGlassesDevice(device.getBleName())) {
                    bleConnect();
                    Log.d(BASETAG, " if (device.getBleName() != null && isGlassesDevice(device.getBleName())) {bleConnect();");
                }
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
            Log.e(T2TAG, "onScanFailed: "+errorCode);
        }
    };

    public void bleConnect() {
        Ble<BleDevice> ble = Ble.getInstance();
        if (ble.isScanning()) {
            ble.stopScan();
        }

        if (bleRssiDevices != null && bleRssiDevices.size() != 0) {
            for(BleRssiDevice device: bleRssiDevices) {
                if (device.getBleName() != null && isGlassesDevice(device.getBleName())){
                    mConnectBle.connect(device, connectCallback);
                }
            }
        } else {
            Log.e(TAG, "bleConnect: Not found connectable device, scan device first");
        }
    }

    private boolean isGlassesDevice(String deviceName) {
        Pattern pattern = Pattern.compile("T2", Pattern.CASE_INSENSITIVE);
        Matcher matcher = pattern.matcher(deviceName);
        return matcher.find();
    }

    private BleConnectCallback<BleDevice> connectCallback = new BleConnectCallback<BleDevice>() {
        @Override
        public void onConnectionChanged(BleDevice device) {
            //Log.e(TAG, "onConnectionChanged: " + device.getConnectionState()+Thread.currentThread().getName());
            if (device.isConnected()) {
//                actionBar.setTitle(device.getBleAddress());
//                actionBar.setSubtitle("connected"); // connected
                mBleDeviceConnectList.add(device);
                mBleDevice = device;
                SeeSettings.setLockedAddress(getApplication(),device.getBleAddress());
                Toast.makeText(getApplication(),"bluetooth connected",Toast.LENGTH_SHORT).show(); // bluetooth connected
            }else if (device.isConnecting()){
//                actionBar.setTitle(device.getBleAddress());
//                actionBar.setSubtitle("connecting..."); // connecting...
            }
            else if (device.isDisconnected()){
                //mBleDevice = null;
                mBleDeviceConnectList.remove(device);
//                actionBar.setTitle("T2Plus");
//                actionBar.setSubtitle("not connected"); // not connected
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
            Log.e(T2TAG, "onConnectCancel: " + device.getBleName());
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

            Log.e(T2TAG, "onReady device==:" + device.getBleAddress());
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

    public void setEnableNotify(boolean enable){
        if (mBleDevice ==null)return;
        mConnectBle.enableNotifyByUuid(mBleDevice,true,read_service_uuid,read_characerisric_uuid, new BleNotifyCallback<BleDevice>() {
            @Override
            public void onChanged(BleDevice device, BluetoothGattCharacteristic characteristic) {
                BleLog.e(T2TAG, "onChanged==data:" + ByteUtils.toHexString(characteristic.getValue()));
                //Log.e(TAG, "onChanged==data:" + ByteUtils.bytes2HexStr(characteristic.getValue()));
                SeeTCmdData cmdData = new SeeTCmdData(mSeeDevice.getType(),characteristic.getValue());
                //mTvLog.setText(cmdData.toString() );
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        T2binding.tvDeviceInfo.setText(cmdData.toString());
//                        parseIp(cmdData);
//                    }
//                });
            }
        });
    }

    /**
     * 连接眼镜设备 Connect glasses device
     */
    private void connectDevice(){
        connect(this.ip);
    }

    private void connect(String ip){
        if (!mSeeDevice.isConnect() ) {
            if (ip != null) {
                mSeeDevice.connect(ip);
            } else {
                Log.e(T2TAG, "connect: IP address not found");
            }

        } else {
            mSeeDevice.disconnect();
        }
    }
}


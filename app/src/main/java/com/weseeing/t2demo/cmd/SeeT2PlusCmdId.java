package com.weseeing.t2demo.cmd;

public class SeeT2PlusCmdId {

    //dev control Function Id
    public static final int kMsgPowerOn = 0x4001;
    public static final int kMsgPowerOff = 0x4002;

    public static final int kMsgShoot = 0x5000; //拍照 拍照：1：单拍 2-N： N 张连拍0：连续连拍
    public static final int kMsgStopShoots = 0x5001;

    public static final int kMsgStartRecVideo = 0x5010; //开始录像
    public static final int kMsgStopRecVideo = 0x5011;//停止录像
    public static final int kMsgStartLive = 0x5020;//开始直播
    public static final int kMsgStopLive = 0x5021;//停止直播

    public static final int kMsgPlayAudio = 0x5032;//提示音播放

    //dev config Function Id
    public static final int kMsgSetWifiParameters = 0xA000; //WIFI参数设置
    public static final int kMsgGetWifiParameters = 0xA001;//WIFI参数获取

    public static final int kMsgSetSoftApParameters = 0xA002; //设置热点
    public static final int kMsgGetSoftApParameters = 0xA003; //获取热点

    public static final int kMsgGetWifiConnect = 0xA004; //WIFI连接

    public static final int kMsgSetSnapParameters = 0xA010;//拍照参数设置
    public static final int kMsgGetSnapParameters = 0xA011; //拍照参数查询

    public static final int kMsgSetVideoParameters = 0xA020;//录像参数设置
    public static final int kMsgGetVideoParameters = 0xA021;//录像参数查询

    public static final int kMsgSetLiveParameters = 0xA030;//视频流参数设置
    public static final int kMsgGetLiveParameters = 0xA031;//视频流参数查询

//    public static final int kMsgSetUserInfoParameters = 0xA060;//用户信息设置
//    public static final int kMsgGetUserInfoParameters = 0xA061;//用户信息查询

    public static final int kMsgGetDevTime = 0xB048;//设备时间查询
    public static final int kMsgSetDevTime = 0xB049;//设备时间设置

    public static final int kMsgStartFtpPut = 0xB030;
    public static final int kMsgStopFtpPut = 0xB031;
    public static final int kMsgGetFtpParameters = 0xB033;
    public static final int kMsgSetFtpParameters = 0xB034;

    public static final int kMsgGetTtxParameters = 0xB036;
    public static final int kMsgSetTtxParameters = 0xB037;
    public static final int kMsgStartTtxMonitor = 0xB038;
    public static final int kMsgStopTtxMonitor = 0xB039;

    //dev status Function Id
    public static final int kMsgRestoreFactory = 0xF000;//恢复出厂设置设置
    public static final int kMsgDevStatus = 0xF001;//设备状态获取
    public static final int kMsgStoreStatus = 0xF002;//设备存储获取

    public static final int kMsgBtGetDevIP = 0xA070; //蓝牙获取设备IP
    public static final int kMsgBtReportDevIP = 0xA071; //蓝牙设备IP上报
    public static final int kMsgNetGetDevIP = 0xA072;  //获取设备IP
    public static final int kMsgNetReportDevIP = 0xA073;//设备IP上报

    public static final int kMsgBtSwitchUvcOn = 0xF003; //蓝牙UVC开
    public static final int kMsgBtSwitchUvcOff = 0xF004; //蓝牙UVC关

    public static final int kMsgNetSwitchUvcOn = 0xF005;//UVC开
    public static final int kMsgNetSwitchUvcOff = 0xF006;//UVC关

    public static final int kMsgStartUpgrade = 0xF007;  //升级固件
    public static final int kMsgNetHeartBeat = 0xF008;  //心跳包
    public static final int kMsgReboot = 0xF011;       //重启设备

    public static final int kMsgSetWifiConnect = 0xA004;

    public static final int kMsgGetDevVolume = 0xA050;
    public static final int kMsgSetDevVolume = 0xA051;
    public static final int kMsgGetDevMute = 0xA052;
    public static final int kMsgSetDevMute = 0xA053;

    public static final int kMsgGetMicVolume = 0xA055;
    public static final int kMsgSetMicVolume = 0xA056;
    public static final int kMsgGetMicMute = 0xA057;
    public static final int kMsgSetMicMute = 0xA058;

    public static final int kMsgGetBatQuantity = 0xB050;

}

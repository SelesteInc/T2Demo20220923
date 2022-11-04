package com.weseeing.t2demo.cmd;

public class SeeT2CmdId {

    //=======================new ==========================
    public static final int kMsgShoot     = 0x0008;//拍照 拍照：1：单拍 2-N： N 张连拍0：连续连拍 Take pictures Take pictures: 1: Single shot 2-N: N-frame continuous shooting 0: Continuous continuous shooting
    public static final int kMsgShootStop = 0x0009; //停止连拍 stop continuous shooting

    public static final int kMsgStartRecVideo = 0x0010; //开始录像 start recording
    public static final int kMsgStopRecVideo = 0x0011;//停止录像 (the following translations are omitted because the variable names are adequate

    public static final int kMsgStartLive = 0x0017;//开始直播
    public static final int kMsgStopLive  = 0x0018;//停止直播

    public static final int kMsgStartTtxMonitor = 0x0019;
    public static final int kMsgStopTtxMonitor = 0x0020;

    public static final int kMsgDevStatus = 0x6001;//眼镜状态获取
    public static final int kMsgDevStatusPut = 0x6002;//眼镜上报状态

    public static final int kMsgSetVideoParameters = 0xB00A;//录像参数设置
    public static final int kMsgGetVideoParameters = 0xB009;//录像参数查询

    public static final int kMsgSetSnapParameters = 0xA010; //拍照参数设置
    public static final int kMsgGetSnapParameters = 0xA011;//拍照参数查询
    public static final int kMsgSetLiveParameters =  0xB018;  //视频流参数设置
    public static final int kMsgGetLiveParameters =  0xB017;  //视频流参数查询

    public static final int kMsgGetFtpParameters = 0xB033 ;//FTP查询
    public static final int kMsgSetFtpParameters  = 0xB034 ;//FTP设置

    public static final int kMsgGetMicVolume = 0xB035;//眼镜麦音量查询
    public static final int kMsgSetMicVolume = 0xB036;//眼镜麦音量设置
    public static final int kMsgGetMicMute = 0xB037;//眼镜麦静音状态查询
    public static final int kMsgSetMicMute = 0xB038;//眼镜麦静音设置

    public static final int kMsgGetDevVolume = 0xB03A ;//眼镜音量查询
    public static final int kMsgSetDevVolume  = 0xB03B ;//眼镜音量设置
    public static final int kMsgGetDevMute = 0xB03C;//眼镜静音状态查询
    public static final int kMsgSetDevMute = 0xB03D;////眼镜静音设置

    public static final int kMsgPlayAudio = 0xB03E;//播放眼镜提示音音频

    public static final int kMsgGetDevLanguage = 0xB041 ;//眼镜语言查询
    public static final int kMsgSetDevLanguage = 0xB042 ;//眼镜语言查询

    public static final int kMsgGetDevTime = 0xB048;//设备时间查询
    public static final int kMsgSetDevTime = 0xB049;//眼镜时间设置

    public static final int kMsgRestoreFactory = 0xB04F;//恢复出厂设置设置
    public static final int kMsgStartUpgrade = 0xC000;//升级
    

    //=======================Old ==========================

    public static final int CMD_POWER_ON  = 0x0001; //开机
    public static final int CMD_POWER_OFF  = 0x0002; //关机 shutdown
//  public static final int CMD_TAKE_PHOTOS  = 0x0008;//拍照 拍照：1：单拍 2-N： N 张连拍0：连续连拍
//  public static final int CMD_START_VIDEO = 0x0010;//开始录像
//  public static final int CMD_STOP_VIDEO = 0x0011;//停止录像
//  public static final int CMD_START_TTX_MONITOR = 0x0019;//开始TTX
//  public static final int CMD_STOP_TTX_MONITOR = 0x0020;//停止TTX

    public static final int CMD_VIDEO_STREAM_CONFIG_QUERY = 0xB017; //视频流参数查询
    public static final int CMD_VIDEO_STREAM_CONFIG_SETTING = 0xB018;//视频流参数配置

    public static final int CMD_AUDIO_STREAM_CONFIG_QUERY = 0xB01E; //音频传输参数查询
    public static final int CMD_AUDIO_STREAM_CONFIG_SETTING = 0xB01F; //音频传输参数配置

    public static final int CMD_WIFI_QUERY = 0xB025; //WIFI 参数查询
    public static final int CMD_WIFI_SETTING = 0xB026; //WIFI 参数配置

    public static final int CMD_START_RECORD = 0x002C;//开始录音
    public static final int CMD_STOP_RECORD = 0x002D;//开始录音

    public static final int CMD_PLAY_MP3 = 0x0033;//开始播放音乐
    public static final int CMD_STOP_MP3 = 0x0034;//停止播放音乐

    //public static final int CMD_GET_GLASSES_STATE = 0x6001;//眼镜状态获取
    //public static final int CMD_GLASSES_STATE = 0x6002;//眼镜上报状态
    public static final int CMD_GLASSES_BATTERY_GET = 0x6008;//眼镜电量
    public static final int CMD_GLASSES_BATTERY = 0x6009;//眼镜上报电量
    public static final int CMD_GLASSES_STORAGE_GET = 0x6010; //存储状态获取
    public static final int CMD_GLASSES_STORAGE = 0x6011; //存储上报状态获取
    public static final int CMD_GLASSES_FILES_GET = 0x601E; //眼镜文件列表获取
    public static final int CMD_GLASSES_FILES = 0x601F; //眼镜上报文件列表
    public static final int CMD_GLASSES_FTP_FILES_GET = 0x6025; //FTP上报文件列表
    public static final int CMD_GLASSES_FTP_FILES = 0x6026; //FTP文件列表上报

    public static final int CMD_SET_MAC = 0xB001; //配置设备地址
    public static final int CMD_SET_NAME = 0xB002; //配置设备名称
    public static final int CMD_STATE  =  0xB003;  //设备上电状态

//  public static final int CMD_VIDEO_PARAMETER_GET  =  0xB009;  //录像参数查询
//  public static final int CMD_VIDEO_PARAMETER_SET  =  0xB00A;  //录像参数设置
//  public static final int CMD_PHOTO_PARAMETER_GET =  0xB010;  //拍照参数查询
//  public static final int CMD_PHOTO_PARAMETER_SET =  0xB011;  //拍照参数设置
//  public static final int CMD_LIVE_STREAM_PARAMETER_GET =  0xB017;  //视频流参数查询
//  public static final int CMD_LIVE_STREAM_PARAMETER_SET =  0xB018;  //视频流参数设置

    public static final int CMD_AUDIO_STREAM_PARAMETER_GET =  0xB01E;  //音频流参数查询
    public static final int CMD_AUDIO_STREAM_PARAMETER_SET =  0xB01F;  //音频流参数设置
    public static final int CMD_WIFI_PARAMETER_GET =  0xB025;  //WIFI参数查询
    public static final int CMD_WIFI_PARAMETER_SET =  0xB026;  //WIFI参数设置
    public static final int CMD_NETWORK_PARAMETER_GET =  0xB02C;  //网络参数查询
    public static final int CMD_NETWORK_PARAMETER_SET =  0xB02D;  //网络参数设置

//    public static final int CMD_MIC_VOLUME_GET = 0xB035 ;//眼镜麦音量查询
//    public static final int CMD_MIC_VOLUME_SET  = 0xB036 ;//眼镜麦音量设置
//    public static final int CMD_MIC_MUTE_GET = 0xB037 ;//眼镜麦静音状态查询
//    public static final int CMD_MIC_MUTE_SET  = 0xB038 ;//眼镜麦静音设置

//    public static final int CMD_VOLUME_SET  = 0xB03B ;//眼镜音量设置
//    public static final int CMD_VOLUME_GET = 0xB03A ;//眼镜音量查询
//    public static final int CMD_MUTE_GET = 0xB03C ;//眼镜静音状态查询
//    public static final int CMD_MUTE_SET  = 0xB03D ;//眼镜静音设置
//    public static final int CMD_GLASSES_AUDIO_PLAY = 0xB03E  ;  //播放眼镜音频
//    public static final int CMD_LANGUAGE_GET = 0xB041 ;//眼镜语言查询
//    public static final int CMD_LANGUAGE_SET  = 0xB042 ;//眼镜语言设置
//    public static final int CMD_TIME_GET = 0xB048 ;//眼镜时间查询
//    public static final int CMD_TIEM_SET  = 0xB049 ;//眼镜时间设置
//    public static final int CMD_FACTORY_RESET = 0xB04F;//恢复出厂设置设置
//    public static final int CMD_SYSTEM_UPGRADE = 0xC000;//升级
    public static final int CMD_UPGRADE_DOWNLOAD = 0xC001;//升级下载
    public static final int CMD_UPGRADE_PROGRESS = 0xC002;//升级进度
}

package com.weseeing.t2demo.cmd;


import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.t.SeeTCmdData;

public class SeeTCmdDataHelperManage {

    /**
     * The time command ID is the same as the parameter, and the same one can be used
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataTime(int deviceType){
        int id = SeeT2CmdId.kMsgSetDevTime;
        String data = SeeTCmdHelper.setTimeJson(System.currentTimeMillis());;
      return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * The ftp ID is the same as the parameter, you can use the same one
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataFtp(int deviceType,String ip,int port,String userName,String pwd){
        int id;
        String data;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetFtpParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgSetFtpParameters;
        }
        data = SeeTCmdHelper.setFtpJson(ip,port,userName,pwd);
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * Get device status
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataDevStatus(int deviceType){
        int id;
        String data=  SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgDevStatus;
        }else{
            id = SeeT2PlusCmdId.kMsgDevStatus;
        }

        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * Reboot the device
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataDevReboot(int deviceType){
        int id = SeeT2PlusCmdId.kMsgReboot;
        String data=  SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
        }else{
        }

        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * shutdown
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataDevShutdown(int deviceType){
        int id = SeeT2PlusCmdId.kMsgPowerOff;
        String data=  SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
        }else{
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * Photograph
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataShoot(int deviceType){
        int id;
        String data;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgShoot;
        }else{
            id = SeeT2PlusCmdId.kMsgShoot;
        }
        data = SeeTCmdHelper.setTakePhotosJson(1);
        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * 开始录像 start recording
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataStarVideo(int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgStartRecVideo;
        }else{
            id = SeeT2PlusCmdId.kMsgStartRecVideo;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 停止录像 stop recording
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataStopVideo(int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgStopRecVideo;
        }else{
            id = SeeT2PlusCmdId.kMsgStopRecVideo;
        }
        return new SeeTCmdData(deviceType,id,data);
    }



    /**
     * 开始录像 start recording (live?)
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataStarLive(int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgStartLive;
        }else{
            id = SeeT2PlusCmdId.kMsgStartLive;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 停止录像 stop recording ()
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataStopLive(int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgStopLive;
        }else{
            id = SeeT2PlusCmdId.kMsgStopLive;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 提示音播放 beep play
     * @param deviceType
     * @param audio_type
     * @return
     */
    public static SeeTCmdData getCmdDataAudioPlay(int deviceType,int audio_type){
        int id ;
        String data = SeeTCmdHelper.setGlassesAudio(audio_type,"");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgPlayAudio;
        }else{
            id = SeeT2PlusCmdId.kMsgPlayAudio;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 获取音量 get volume
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetVolume (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");;
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetDevVolume;
        }else{
            id = SeeT2PlusCmdId.kMsgGetDevVolume;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 获取音量 get volume
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataSetVolume (int deviceType,int volume){
        int id;
        String data = SeeTCmdHelper.setVolumeJson(volume);
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetDevVolume;
        }else{
            id = SeeT2PlusCmdId.kMsgSetDevVolume;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 设置静音模式 Set silent mode
     * @param deviceType
     * @param mute   0 is unmute, 1 is mute
     * @return
     */
    public static SeeTCmdData getCmdDataSetMute (int deviceType,int mute){
        int id;
        String data = SeeTCmdHelper.setMuteJson(mute);
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetDevMute;
        }else{
            id = SeeT2PlusCmdId.kMsgSetDevMute;
        }
        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * 查询静音模式 Query silent mode
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetMute (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetDevMute;
        }else{
            id = SeeT2PlusCmdId.kMsgGetDevMute;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 设置静音模式 Set silent mode
     * @param deviceType
     * @param mute   0 is unmute, 1 is mute
     * @return
     */
    public static SeeTCmdData getCmdDataSetMicMute (int deviceType,int mute){
        int id;
        String data = SeeTCmdHelper.setMuteJson(mute);
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetMicMute;
        }else{
            id = SeeT2PlusCmdId.kMsgSetMicMute;
        }
        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * 查询静音模式 Query silent mode
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetMicMute (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetMicMute;
        }else{
            id = SeeT2PlusCmdId.kMsgGetMicMute;
        }
        return new SeeTCmdData(deviceType,id,data);
    }

//    /**
//     * 设置语言 language setting
//     * @param deviceType
//     * @return
//     */
//    public static SeeTCmdData getCmdDataSetLanguage (int deviceType,int language){
//        int id = SeeT2CmdId.kMsgGetDevLanguage;
//        String data = SeeTCmdHelper.setLanguage(language);
//        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
//        }else{
//        }
//        return new SeeTCmdData(deviceType,id,data);
//    }

    /**
     * 获取拍照参数 language setting
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataSetPhoto (int deviceType,int width,int height){
        int id;
        String data = SeeTCmdHelper.setPhotoJson(width,height,".jpeg");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetSnapParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgSetSnapParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 查询拍照参数 Query camera parameters
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetPhoto (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetSnapParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgGetSnapParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 录像参数 Recording parameters
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataSetVideo (int deviceType,int width,int height,int fileDuration,int fps){
        int id;
        String data = SeeTCmdHelper.setVideoJson(width,height,fileDuration,fps);
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetVideoParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgSetVideoParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * 查询拍照参数 Query camera parameters
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetVideo (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetVideoParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgGetVideoParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 设置直播参数 Set live broadcast parameters
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataSetLive (int deviceType,int width,int height,int videoType,int audioEnabled,int fps){
        int id;
        String data = SeeTCmdHelper.setLiveJson(width,height,videoType,audioEnabled,1,fps);
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgSetLiveParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgSetLiveParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }

    /**
     * 查询拍照参数 Query camera parameters
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetLive (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgGetLiveParameters;
        }else{
            id = SeeT2PlusCmdId.kMsgGetLiveParameters;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 恢复出厂设置 reset
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetRestoreFactory (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgRestoreFactory;
        }else{
            id = SeeT2PlusCmdId.kMsgRestoreFactory;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


    /**
     * 恢复出厂设置 reset
     * @param deviceType
     * @return
     */
    public static SeeTCmdData getCmdDataGetStartUpgrade (int deviceType){
        int id;
        String data = SeeTCmdHelper.setDefaultJson("");
        if (deviceType == SeeDeviceChannel.TYPE_T2_DEVICE){
            id = SeeT2CmdId.kMsgStartUpgrade;
        }else{
            id = SeeT2PlusCmdId.kMsgStartUpgrade;
        }
        return new SeeTCmdData(deviceType,id,data);
    }


}

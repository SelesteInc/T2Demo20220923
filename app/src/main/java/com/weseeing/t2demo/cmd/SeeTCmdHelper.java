package com.weseeing.t2demo.cmd;

import org.json.JSONException;
import org.json.JSONObject;

public class SeeTCmdHelper {
    private static final String TAG = "SeeT.SeeT2CmdHelper";

    /**
     * 默认数据 default data
     * @param value
     * @return
     */
    public static String setDefaultJson(String value){
        JSONObject object = new JSONObject();
        try {
            object.put("default",value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }


    /*
     *设置语言类型 set language type
     * @param languageType 0  中文，1英语
     * @return
     */
    public static String setLanguage(int languageType){
        JSONObject object = new JSONObject();
        try {
            object.put("language",languageType);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }


    /**
     *设置拍照参数 Set camera parameters
     * @param width 宽
     * @param height 高
     * @param fileDuration 文件时长
     * @ param fps T2Plus 参数 (录像，直播统一生效)
     */
    public static String setVideoJson(int width, int height, int fileDuration, int fps){
        JSONObject object = new JSONObject();
        try {
            object.put("width",width);
            object.put("height",height);
            object.put("time_per_file",fileDuration);
            object.put("fps",fps);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return  object.toString();
    }

    /**
     *设置拍照参数 Set camera parameters
     * @param width 宽
     * @param height 高
     * @param fileFormat 生产文件类型.jpeg
     */
    public static String setPhotoJson(int width,int height,String fileFormat){
        JSONObject object = new JSONObject();
        try {
            object.put("width",width);
            object.put("height",height);
            object.put("file_format",fileFormat);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return  object.toString();
    }

    /**
     * 直播参数设置 Live broadcast parameter settings
     * @param width 宽
     * @param height 高
     * @param videoType  视频流编码格式 264 H264 ， 265 H265 (录像，直播统一生效) Video stream encoding format 264 H264 , 265 H265 (recording and live broadcast are unified)
     * @param audioEnabled   Audio 1 on, off
     * @param audioType  音频类型  1 aac，设备只支持aac  此参数留给扩展使用 Audio type 1 aac, the device only supports aac This parameter is reserved for extended use
     * @return
     */
    public  static String setLiveJson(int width,int height,int videoType,int audioEnabled,int audioType,int fps){
        JSONObject object = new JSONObject();
        try {
            object.put("width",width);
            object.put("height",height);
            object.put("video_type",videoType);
            object.put("audio",audioEnabled);
            object.put("audio_type",audioType);
            object.put("fps",fps);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }


    /**
     * 设置Name set name
     * @param name
     * @return
     */
    public static String setNameJson(String  name){
        JSONObject object = new JSONObject();
        try {
            object.put("name",name);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }


    /**
     * 设置 sn串号 set sn serial number
     * @param mac
     * @return
     */
    public static String setMacJson(String  mac){
        JSONObject object = new JSONObject();
        try {
            object.put("mac",mac);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     * 设置音量 set volume
     * @param volume
     * @return
     */
    public static String setVolumeJson(int volume){
        JSONObject object = new JSONObject();
        try {
            object.put("volume",volume);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     * 设置静音 set mute
     * @param value   0 is unmute, 1 is mute
     * @return
     */
    public static String setMuteJson(int value){
        JSONObject object = new JSONObject();
        try {
            object.put("mute",value);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     *ftp设置参数 ftp setting parameters
     * @param ip  眼镜ip地址
     * @param port 端口
     * @param userName
     * @param pwd
     * @param T2plus  参数 boot_start 1 开机启动 Parameter boot_start 1 Boot start
     * @return
     */
    public static String setFtpJson(String ip, int port, String userName, String pwd){
        JSONObject object = new JSONObject();
        try {
            object.put("ip",ip);
            object.put("port",port);
            object.put("name",userName);
            object.put("pwd",pwd);
            object.put("boot_start",0);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 网络配置 Network Configuration
     * @param ssid  WiFi Ssid
     * @param pwd  WiFi密码
     *  @param pwd I  热点模式类型IP Hotspot Mode Type IP
     * @return
     */
    public static String setWiFiJsonT2(String ssid,String pwd){
        JSONObject stringer = new JSONObject();
        try {
            stringer.put("S",ssid);
            stringer.put("P",pwd);
            //stringer.put("I","1");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringer.toString();
    }

    /**
     * 网络配置 Network Configuration
     * @param ssid  WiFi Ssid
     * @param psk  WiFi密码
     * @return
     */
    public static String setWiFiJsonT2Plus(String ssid, String psk){
        JSONObject stringer = new JSONObject();
        try {
            stringer.put("ssid",ssid);
            stringer.put("psk",psk);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return stringer.toString();
    }

    /**
     * 连接成功发送此命令，通知眼镜停止广播设备发现 When the connection is successful send this command to inform the glasses to stop broadcasting device discovery
     * @param value
     * @return
     */
    public static String setPowerJson(String value){
        JSONObject object = new JSONObject();
        try {
            object.put("sn_mac",value);
            object.put("msg","T2_BROADCAST_ACK");
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     * 拍照 Photograph
     * @param number 连拍1-N  1单拍  0连拍 Continuous shooting 1-N 1 single shooting 0 continuous shooting
     * @return
     */
    public static String setTakePhotosJson(int number){

        JSONObject object = new JSONObject();
        try {
            object.put("continuous_number",number);
        } catch (JSONException e) {
            e.printStackTrace();
        }catch (Exception e){
        }

        return object.toString();
    }

    /**
     * 设置眼镜时间 Set glasses time
     * @param time System.currentTimeMillis()
     * @return
     */
    public static String  setTimeJson(long time ){
        JSONObject object = new JSONObject();
        try {
            object.put("timestamp",time);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return object.toString();
    }

    /**
     * 设置通天星服务器IP和设备ID Set Babel Server IP and Device ID
     * @param serverIp
     * @param deviceId
     * @return
     */
    public static String setTtxJson(String serverIp,String deviceId){
        JSONObject object = new JSONObject();
        try {
            object.put("server_ip",serverIp);
            object.put("device_id",deviceId);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

    /**
     *@param type <= 0  表示用后面的audio_name  type> 0 ,表示用列表表的序列 Indicates that the following audio_name type> 0 is used to indicate the sequence of the list table
     *@param audioName
     * @return
     */
    public static String setGlassesAudio(int type, String audioName){
        JSONObject object = new JSONObject();
        try {
            object.put("audio_type",type);
            object.put("audio_name",audioName);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        return object.toString();
    }

}

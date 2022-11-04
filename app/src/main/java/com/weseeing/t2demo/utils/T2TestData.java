//package com.weseeing.t2demo.utils;
//
//import android.content.Context;
//import android.net.DhcpInfo;
//import android.net.wifi.WifiInfo;
//import android.net.wifi.WifiManager;
//import android.os.Handler;
//import android.text.TextUtils;
//import android.util.Log;
//
//import com.weseeing.framework.t.SeeTCmdData;
//import com.weseeing.t2demo.ui.adapter.MediaData;
//
//import org.json.JSONException;
//import org.json.JSONObject;
//
//import java.io.File;
//import java.net.InetAddress;
//import java.net.InterfaceAddress;
//import java.net.NetworkInterface;
//import java.net.SocketException;
//import java.util.ArrayList;
//import java.util.Collections;
//import java.util.Comparator;
//import java.util.Enumeration;
//import java.util.List;
//
//public class T2TestData {
//
//
//    public static int [] itemId = {0x0001,0x0002,0x0008,0x0009,0x0010,0x0011,0x0017,0x0018,0xB017,0xB018,0xB025,0xB026,
//            0xB033,0xB034,0x0033,0x0034,0xB049,0x6001,0x6008,0x6010,0xB03A,0xB03B,0xB03E};
//    public static String [] contentArray = {"眼镜开机","眼镜关机","拍照","停止连拍","开始录像","停止录像"
//            ,"启动直播","停止直播","视频流查询","视频流设置","WIFI查询","WIFI设置","FTP查询","FTP设置"
//            ,"开始播放","停止播放","设置时间","眼镜状态","电量","存储","音量","音量设置","眼镜音频"};

//{"Glasses on", "Glasses off", "Take pictures", "Stop continuous shooting", "Start recording", "Stop recording"
// ,"Start live broadcast","stop live broadcast","video stream query","video stream settings","WIFI query","WIFI settings","FTP query","FTP settings"
// ,"start playback","stop playback","set time","glasses status","battery","storage","volume","volume setting","glasses audio"}

//
//    public static final int CMD_GLASSES_AUDIO_PLAY = 0xB03E  ;  //播放眼镜音频 Play glasses audio
//
//    public static SeeTCmdData getTestData(Context context,int id,String value){
//        String returnValue = "";
//        switch (id){
//            case  SeeTCmdData.CMD_TAKE_PHOTOS:
//                returnValue = getTakePhotos(value);
//                break;
//            case SeeTCmdData.CMD_POWER_OFF:
//            case SeeTCmdData.CMD_POWER_ON:
//               returnValue = getPower(value);
//               break;
//            case SeeTCmdData.CMD_FTP_SETTING:
//                returnValue = getFtp(context,value);
//                break;
//            case SeeTCmdData.CMD_WIFI_SETTING:
//                returnValue =getWiFiJson();
//                break;
//            case 0xB049:
//                returnValue =  getTime();
//                    break;
//            case 0xB03B:
//                returnValue = setVolumeJson(100);
//                break;
//            case 0xB03E:
//                returnValue = value;
//                break;
//            case 0x6008:
//            case 0x6001:
//            case 0x6010:
//
//            default:
//                returnValue = getDefault(value);
//                break;
//        }
//
//        Log.d("T2TestData","returnValue: "+returnValue);
//        SeeTCmdData wiFiCmdData = new SeeTCmdData(id,returnValue);
//        return wiFiCmdData;
//    }
//
//
//    public static String getGlassesAudio(){
//        JSONObject object = new JSONObject();
//        try {
//            object.put("audio_type",1);
//            object.put("audio_name","");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object.toString();
//    }
//
//    /**
//     * 设置音量 set volume
//     * @param volume
//     * @return
//     */
//    public static String setVolumeJson(int volume){
//        JSONObject object = new JSONObject();
//        try {
//            object.put("volume",volume);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object.toString();
//    }
//
//    public static String  getTime(){
//        JSONObject object = new JSONObject();
//        try {
//            object.put("timestamp",System.currentTimeMillis());
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object.toString();
//    }
//
//    public static String getFtp(Context context,String value){
//        JSONObject object = new JSONObject();
//        try {
//            if (TextUtils.isEmpty(value)){
//                value = getIp(context);
//            }
////            object.put("ftp_ip",value);
////            object.put("port",2222);
////            object.put("user_name","admin");
////            object.put("password","SeeU123456");
//            object.put("ip",value);
//            object.put("port",2222);
//            object.put("name","admin");
//            object.put("pwd","SeeU123456");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return object.toString();
//    }
//
//
//
//    public static String getTakePhotos(String value){
//        if (TextUtils.isEmpty(value)){
//            value = "1";
//        }
//        JSONObject object = new JSONObject();
//        try {
//            int number = 1;
//            try{
//                number= Integer.valueOf(value);
//            }catch (NumberFormatException e){
//                number =1;
//            }
//            object.put("continuous_number",number);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }catch (Exception e){
//        }
//
//        return object.toString();
//    }
//
//    public static String getDefault(String value){
//        JSONObject object = new JSONObject();
//        try {
//            object.put("default",value);
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object.toString();
//    }
//
//    public static String getPower(String value){
//        JSONObject object = new JSONObject();
//        try {
//            object.put("sn_mac",value);
//            object.put("msg","T2_BROADCAST_ACK");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//
//        return object.toString();
//    }
//
//    public static String getWiFiJson(){
//        JSONObject stringer = new JSONObject();
//        try {
//            stringer.put("S","see360");
//            stringer.put("P","Kj123456");
////            stringer.put("I","1");
//        } catch (JSONException e) {
//            e.printStackTrace();
//        }
//        return stringer.toString();
//    }
//
//    public static String getFtpUrlInfo(Context context){
//        StringBuffer info = new StringBuffer();
//        info.append("ftp://");
//        info.append(getIp(context));
//        info.append(":");
//        info.append(2222);
//        info.append("\n");
//        info.append("user: admin");
//        info.append(" password: SeeU123456");
//        return info.toString();
//    }
//
//    private static String getIp(Context context){
//        //获取wifi服务
//        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
//        //判断wifi是否开启
//        if (!wifiManager.isWifiEnabled()) {
//            //wifiManager.setWifiEnabled(true);
//        }
//        WifiInfo wifiInfo = wifiManager.getConnectionInfo();
//        int ipAddress = wifiInfo.getIpAddress();
//        DhcpInfo info=wifiManager.getDhcpInfo();
//        Log.e("xlh","ss:"+intToIp(info.serverAddress));
//        String ip = intToIp(ipAddress);
//        return ip;
//    }
//
//    private static  String intToIp(int i) {
//
//        return (i & 0xFF ) + "." +
//                ((i >> 8 ) & 0xFF) + "." +
//                ((i >> 16 ) & 0xFF) + "." +
//                ( i >> 24 & 0xFF) ;
//    }
//
//}

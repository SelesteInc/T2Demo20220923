package com.weseeing.t2demo.utils;

import android.content.Context;
import android.util.Log;

import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

public class Utils {
    private final static String TAG = "Utils";

    public static String getLocalIp()  {
        String ipAddress = "0.0.0.0";
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements(); ) {
                NetworkInterface intf = en.nextElement();
                List<InterfaceAddress> mList = intf.getInterfaceAddresses();
                for (InterfaceAddress l : mList) {
                    InetAddress inetAddress = l.getAddress();
                    if (!inetAddress.isLoopbackAddress()) {
                        String hostAddress = inetAddress.getHostAddress();
                        if (hostAddress.indexOf(":") > 0) {
                            continue;
                        } else {
                            ipAddress = hostAddress;
                            Log.d(TAG,"ipAddress:"+ipAddress );
                            return ipAddress;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
            Log.d(TAG,"SocketException:"+e.toString());
        }
        return ipAddress;
    }

    public static String getFtpUrlInfo(){
        StringBuffer info = new StringBuffer();
        info.append("ftp://");
        info.append(getLocalIp());
        info.append(":");
        info.append(2222);
        info.append("\n");
        info.append("user: admin");
        info.append(" password: SeeU123456");
        return info.toString();
    }
}

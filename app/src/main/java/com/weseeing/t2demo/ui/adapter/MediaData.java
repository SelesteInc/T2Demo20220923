package com.weseeing.t2demo.ui.adapter;

import java.io.Serializable;

/**
 * @author LinHui.Xiao
 * @data Created by 2016/11/29 17:06
 */

public class MediaData implements Serializable {
    private static final long serialVersionUID = -3107682937677416413L;
    public static final int TYPE_PICTURE = 1;
    public static final int TYPE_VIDEO = 2;

    public static final int STATE_THUNMNAIL = 0;
    public static final int STATE_DOWNLOADED = 1;
    public static final int STATE_DOWNLOADING = 2;

    public String url;
    public String name;
    public String ip ;
    public int type ;
    private boolean mChecked;
    private String mDownloadProgress = "0%";
    private int mCurrentState = STATE_THUNMNAIL;


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public boolean isChecked() {
        return mChecked;
    }

    public void setChecked(boolean mChecked) {
        this.mChecked = mChecked;
    }

    public String getDownloadProgress() {
        return mDownloadProgress;
    }

    public void setDownloadProgress(String mDownloadProgress) {
        this.mDownloadProgress = mDownloadProgress;
    }

    public int getCurrentState() {
        return mCurrentState;
    }

    public void setCurrentState(int mCurrentState) {
        this.mCurrentState = mCurrentState;
    }

    public String getIp() {
        return ip;
    }

    public void setIp(String ip) {
        this.ip = ip;
    }


    public int getType() {
        return type;
    }

    public void setType(int type) {
        this.type = type;
    }

}

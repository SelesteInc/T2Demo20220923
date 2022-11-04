package com.weseeing.t2demo.ui.fragment;


import android.app.Fragment;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.preference.ListPreference;
import android.preference.Preference;
import android.preference.PreferenceFragment;
import android.preference.PreferenceManager;
import android.preference.SwitchPreference;
import android.util.Log;
import android.widget.SeekBar;
import android.widget.Toast;
import androidx.annotation.NonNull;
import com.afollestad.materialdialogs.DialogAction;
import com.afollestad.materialdialogs.MaterialDialog;
import com.weseeing.framework.SeeDeviceChannel;
import com.weseeing.framework.SeeDeviceChannelCallback;
import com.weseeing.framework.common.SeeTCommState;
import com.weseeing.framework.t.SeeTCmdData;
import com.weseeing.framework.t.SeeTCmdManage;
import com.weseeing.framework.t.SeeTDataInfo;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.cmd.SeeT2CmdId;
import com.weseeing.t2demo.cmd.SeeT2PlusCmdId;
import com.weseeing.t2demo.cmd.SeeTCmdDataHelperManage;

import org.json.JSONException;
import org.json.JSONObject;
import java.text.DecimalFormat;

public class TGlassesSettingFragment extends PreferenceFragment implements Preference.OnPreferenceClickListener, Preference.OnPreferenceChangeListener, SeeDeviceChannelCallback {
        private  static  final String TAG = "GlassesSettingFragment";

    public static final int LANGUAGE_ZH = 0;
    public static final int LANGUAGE_US = 1;

    private int mIsDefaultLanguage = 0;
    private SharedPreferences mPrefs;
    private Preference mStorage,mBattery,mResetSettings,mUpgrade,mGlassesIp,mVolume,mFps,mVideoType; //
    private SwitchPreference mMuteEnabled,mMicMuteEnabled;

    private int mCurrentVolume = 60;

    private ListPreference mGlassesPhoto,mGlassesVideo,
            mVideoDuration,mLiveResolution; //mLanguage,

    /**
     * 设备通信 Device communication
     */
    private SeeDeviceChannel mSeeDevice;
    private int mDeviceType = SeeDeviceChannel.TYPE_T2_DEVICE;

    public TGlassesSettingFragment() {
    }

    public static Fragment newInstance() {
        Fragment fragment = new TGlassesSettingFragment();
        Bundle args = new Bundle();
        fragment.setArguments(args);
        return fragment;
    }

    public Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            Log.i(TAG,"type:"+msg.what);
            super.handleMessage(msg);
            switch (msg.what) {
                default:
                    Log.i(TAG,"Default.");
                    break;
            }
        }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.setting_t_glasses_preference);

        //设备通信管理 Device Communication Management
        mSeeDevice = SeeDeviceChannel.create(getActivity(), SeeDeviceChannel.isBindType(getActivity()),null);
        mSeeDevice.addChannelCallback(this);
        mDeviceType = mSeeDevice.getType();
        initView();
        init();
    }

    @Override
    public void onResume() {
        super.onResume();
        initCmd();
    }


    private void init() {
        mPrefs = PreferenceManager.getDefaultSharedPreferences(getActivity());

//        mLanguage.setSummary(mPrefs.getString("TLanguage",""));
        mGlassesPhoto.setSummary(mPrefs.getString("TGlassesPhoto",""));
        mGlassesVideo.setSummary(mPrefs.getString("TGlassesVideo",""));
        mLiveResolution.setSummary(mPrefs.getString("TLiveResolution",""));
        mUpgrade.setSummary(mPrefs.getString("TUpgrade",""));
        mVideoDuration.setSummary(getVideoDuration(mPrefs.getString("TVideoDuration","")));
        mVolume.setSummary(""+ mPrefs.getInt("TVolume",mCurrentVolume));
    }

    private void initView() {
        mStorage = findPreference("TStorage");
        mBattery = findPreference("TBattery");
        mResetSettings =findPreference("TResetSettings");
        mUpgrade = findPreference("TUpgrade");
        mGlassesIp = findPreference("TGlassesIp");
        mVolume = findPreference("TVolume");


        mVolume.setOnPreferenceClickListener(this);
        mUpgrade.setOnPreferenceClickListener(this);
        mResetSettings.setOnPreferenceClickListener(this);
//        mLanguage =  (ListPreference) findPreference("TLanguage");
//        mLanguage.setOnPreferenceChangeListener(this);

        mGlassesPhoto  = (ListPreference) findPreference("TGlassesPhoto");
        mGlassesPhoto.setOnPreferenceChangeListener(this);
        mLiveResolution = (ListPreference) findPreference("TLiveResolution");
        mLiveResolution.setOnPreferenceChangeListener(this);
        mVideoDuration = (ListPreference) findPreference("TVideoDuration");
        mVideoDuration.setOnPreferenceChangeListener(this);
        mGlassesVideo = (ListPreference) findPreference("TGlassesVideo");
        mGlassesVideo.setOnPreferenceChangeListener(this);
        mFps= findPreference("TFps");
        mVideoType = findPreference("TVideoType");
        mFps.setOnPreferenceChangeListener(this);
        mVideoType.setOnPreferenceChangeListener(this);

        mMuteEnabled = (SwitchPreference) findPreference("TMuteEnabled");
        mMuteEnabled.setOnPreferenceChangeListener(this);

        mMicMuteEnabled = (SwitchPreference) findPreference("TMicMuteEnabled");
        mMicMuteEnabled.setOnPreferenceChangeListener(this);
    }


    private void initCmd(){
        //获取设备状态 Get device status
        sendCmd(SeeTCmdDataHelperManage.getCmdDataDevStatus(mDeviceType));
        //sendCmd(SeeTCmdDataHelperManage.getCmdDataGetVolume(mDeviceType));

        mGlassesIp.setSummary(mSeeDevice.getAddress());
    }



    private boolean isShowConnectHint(){
        if (mSeeDevice.isConnect())return  true;
        Toast.makeText(getActivity(),"眼镜已断开连接", Toast.LENGTH_SHORT).show(); // Glasses are disconnected
        return  false;
    }

    private void sendCmd(SeeTCmdData cmdData){
        if (!isShowConnectHint())return;
        mSeeDevice.sendCmdData(cmdData);
    }

    private void sendCmd(int id,String data){
        sendCmd(new SeeTCmdData(mDeviceType,id,data));
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }

    @Override
    public boolean onPreferenceChange(Preference preference, Object newValue) {
        String key = preference.getKey();
//        if ("TLanguage".equals(key)){
//            mLanguage.setSummary((String) newValue);
//            setLanguageForGlass((String) newValue);
//        }else
        if ("TGlassesPhoto".equals(key)){
            String value = (String) newValue;
            String resolutionList[] =  value.split("\\*");
            //设置照片参数 Set photo parameters
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetPhoto(mDeviceType,Integer.valueOf(resolutionList[0]), Integer.valueOf(resolutionList[1])));
            mGlassesPhoto.setSummary((String) newValue);
            //测试--查询照片参数 Test -- query photo parameters
            //sendCmd(SeeTCmdDataHelperManage.getCmdDataGetPhoto(mType));

        }else if ("TVideoDuration".equals(key)){
            String value = (String) newValue;
            String resolution=  mPrefs.getString("TGlassesVideo","1280*720");
            String resolutionList[] =  resolution.split("\\*");
            int width =  Integer.valueOf(resolutionList[0]);
            int height = Integer.valueOf(resolutionList[1]);
            int fileDuration = Integer.valueOf(value);
            int fps = Integer.valueOf(mPrefs.getString("TFps","25"));
            //设置录像时长 Set the recording duration
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetVideo(mDeviceType,width,height,fileDuration,fps));
            mVideoDuration.setSummary(getVideoDuration(""+fileDuration));
            //测试--查询录像时长 Test--Query the recording duration
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetVideo(mDeviceType));
        }else if ("TFps".equals(key)){
            String value = (String) newValue;
            String resolution=  mPrefs.getString("TGlassesVideo","1280*720");
            String resolutionList[] =  resolution.split("\\*");
            int width =  Integer.valueOf(resolutionList[0]);
            int height = Integer.valueOf(resolutionList[1]);
            int fileDuration = Integer.valueOf(mPrefs.getString("TVideoDuration","300"));
            int fps = Integer.valueOf(value);
            //设置帧数 set frame rate
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetVideo(mDeviceType,width,height,fileDuration,fps));
            mFps.setSummary( fps == 25? "25帧":"15帧");
            //测试--查询帧数 Test -- Query the number of frames
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetVideo(mDeviceType));

        } else if ("TGlassesVideo".equals(key)){
            String value = (String) newValue;
            String resolutionList[] =  value.split("\\*");
            int width =  Integer.valueOf(resolutionList[0]);
            int height = Integer.valueOf(resolutionList[1]);
            int fileDuration = Integer.valueOf(mPrefs.getString("TVideoDuration","300"));

            //设置录像分辨率 Set the recording resolution
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetVideo(mDeviceType,width,height,fileDuration,25));
            mGlassesVideo.setSummary((String) newValue);
            //测试--查询录像参数 Test--Query recording parameters
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetVideo(mDeviceType));
        } else if ("TVideoType".equals(preference.getKey())){
            String value = (String) newValue;
            int videoType = Integer.valueOf(value);

            String resolution=  mPrefs.getString("TLiveResolution","1280*720");
            String resolutionList[] =  resolution.split("\\*");
            int width =  Integer.valueOf(resolutionList[0]);
            int height = Integer.valueOf(resolutionList[1]);
            int audioEnabled = mPrefs.getBoolean("TLiveAudioEnabled",true) ? 1:0;
            int fps = Integer.valueOf(mPrefs.getString("TFps","25"));
            //设置编码类型 set encoding type
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetLive(mDeviceType,width,height,videoType,audioEnabled,fps));
            mVideoType.setSummary("H"+ ((String) newValue));
            //测试--查询编码类型 test--query encoding type
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetLive(mDeviceType));
        } else if ("TLiveResolution".equals(preference.getKey())){
            String value = (String) newValue;
            String resolutionList[] =  value.split("\\*");
            int width =  Integer.valueOf(resolutionList[0]);
            int height = Integer.valueOf(resolutionList[1]);
            int videoType = Integer.valueOf(mPrefs.getString("TVideoType","264"));
            int audioEnabled = mPrefs.getBoolean("TLiveAudioEnabled",true) ? 1:0;
            int fps = Integer.valueOf(mPrefs.getString("TFps","25"));
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetLive(mDeviceType,width,height,videoType,audioEnabled,fps));
            mLiveResolution.setSummary((String) newValue);
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetLive(mDeviceType));
        } else if ("TMuteEnabled".equals(preference.getKey())){
            boolean value = (boolean) newValue;
            int audioEnabled = value ? 1:0;
            //设置静音开关 Set the mute switch
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetMute(mDeviceType,audioEnabled));
            mMuteEnabled.setSummary(value?"静音模式":"取消静音");
            //测试--查询 test--query
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetMute(mDeviceType));

        } else if ("TMicMuteEnabled".equals(preference.getKey())){
            boolean value = (boolean) newValue;
            int audioEnabled = value ? 1:0;
            //设置Mic静音开关 Set Mic mute switch
            sendCmd(SeeTCmdDataHelperManage.getCmdDataSetMicMute(mDeviceType,audioEnabled));
            mMuteEnabled.setSummary(value?"MIC静音模式":"取消MIC静音");
            //测试--查询 test--query
            sendCmd(SeeTCmdDataHelperManage.getCmdDataGetMicMute(mDeviceType));
        }
        return true;
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        if ("TResetSettings".equals(preference.getKey())){
            resetSettings();
        }else if ("TUpgrade".equals(preference.getKey())){
            UpdateSettings();
        }else if("TVolume".equals(preference.getKey())){
            showVolume();
        }
        return true;
    }


    int tempVolume;
    private void showVolume(){
        SeekBar viewSeeBar = new SeekBar(getActivity());
        viewSeeBar.setMax(100);
        viewSeeBar.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
               if (isShowConnectHint()) {
                   tempVolume = progress;
                   mVolume.setSummary(""+tempVolume);
               }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }
        });
        new MaterialDialog.Builder(getActivity()).title(R.string.volume).customView(viewSeeBar,false)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sendCmd(SeeTCmdDataHelperManage.getCmdDataSetVolume(mDeviceType,tempVolume));
                        mVolume.setSummary(""+tempVolume);
                        mPrefs.edit().putInt("TVolume",tempVolume).commit();
                        mCurrentVolume =tempVolume;
                    }
                }).show();

        int volume = mPrefs.getInt("TVolume",mCurrentVolume);
        viewSeeBar.setProgress(volume);
    }



//    private void setLanguageForGlass(String languageType){
//        if (languageType.equals(getString(R.string.language_zh))){
//            mIsDefaultLanguage = LANGUAGE_ZH;
//        }else if(languageType.equals(getString(R.string.language_us))){
//            mIsDefaultLanguage = LANGUAGE_US;
//        }
//
//        sendCmd(SeeTCmdDataHelperManage.getCmdDataSetLanguage(mType,mIsDefaultLanguage));
//    }

    private void resetSettings() {
        new MaterialDialog.Builder(getActivity())
                .title(R.string.factory_reset)
                .content("提示：恢复出厂设置眼镜会关机，请手动按键开机重新配网使用！")
                .negativeText(R.string.cancel)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sendCmd(SeeTCmdDataHelperManage.getCmdDataGetRestoreFactory(mDeviceType));
                    }
                })
            .show();
    }


    private void UpdateSettings() {
        new MaterialDialog.Builder(getActivity())
                .content("确认升级固件已拷贝到眼镜指定目录！")
                .negativeText(R.string.cancel)
                .positiveText(R.string.ok)
                .onPositive(new MaterialDialog.SingleButtonCallback() {
                    @Override
                    public void onClick(@NonNull MaterialDialog dialog, @NonNull DialogAction which) {
                        sendCmd(SeeTCmdDataHelperManage.getCmdDataGetStartUpgrade(mDeviceType));
                    }
                })
             .show();
    }



    public String getVideoDuration(String newValue){
        String videoDuration  [] = getResources().getStringArray(R.array.videoDuration);
        int   index = 0;
        switch (newValue){
            case "30":
                index = 0;
                break;
            case "60":
                index = 1;
                break;
            case "180":
                index = 2;
                break;
            case "300":
                index = 3;
                break;
            case "600":
                index = 4;
                break;
            case "-1":
                index = 5;
                break;
        }
        return  videoDuration[index];
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mSeeDevice.removeChannelCallback(this);
    }

    @Override
    public void onReceive(Object data) {
        if ( null!=data && (data instanceof SeeTDataInfo)){
            SeeTDataInfo dataInfo = (SeeTDataInfo)data;
            try {
                handleData(dataInfo);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    public void handleData(SeeTDataInfo data) throws JSONException {
        switch (data.getState()) {
            case SeeTCommState.RECEIVE_SUCCESS:
                SeeTCmdData cmdData = data.getData();
                if (cmdData == null)return;
                Log.d(TAG,""+cmdData.toString());
                String sJons = new String(cmdData.getData());
                switch (cmdData.getCommandID()){
                    case SeeT2CmdId.kMsgDevStatus:
                    case SeeT2PlusCmdId.kMsgDevStatus:
                        updateInfo(sJons);
                        break;
                    case SeeT2CmdId.kMsgGetDevVolume:
                    case SeeT2PlusCmdId.kMsgGetDevVolume:
                        updateInfo(sJons);
                        break;
                    case  SeeT2CmdId.kMsgRestoreFactory:
                    case  SeeT2PlusCmdId.kMsgRestoreFactory:
                        Toast.makeText(getActivity(),"恢复出厂成功!重新连接设备", Toast.LENGTH_SHORT).show(); // Factory reset successful! Reconnect the device
                        mSeeDevice.unbind();
                        getActivity().finish();
                        break;
                }
                break;
            default:
                break;
        }
    }


    /**
     * 更新眼镜状态 Update glasses status
     */
    private void updateInfo(String dataJosn) {
        DecimalFormat df = new DecimalFormat("#.00");
        try {
            JSONObject jsonObject = new JSONObject(dataJosn);
            if (jsonObject.has("totaldisk") && jsonObject.has("availdisk") ){
                int totaldisk = jsonObject.getInt("totaldisk");
                int availdisk = jsonObject.getInt("availdisk");
                String info = String.format(getString(R.string.storage_space_used),
                        df.format((double)  availdisk/1024) + "GB",
                        df.format((double) totaldisk/1024) + "GB");
                mStorage.setSummary(info);
            }

            if (jsonObject.has("quantity")){
                mBattery.setSummary( jsonObject.getInt("quantity") + "%" );
            }

            if (jsonObject.has("voltage")){
                mBattery.setSummary( jsonObject.getInt("voltage") + "%");
            }

            if (jsonObject.has("sw_ver")){
                String version = jsonObject.getString("sw_ver");
                mUpgrade.setSummary(version);
            }

            if (jsonObject.has("volume")){
               mCurrentVolume = jsonObject.getInt("volume");
               mVolume.setSummary(""+mCurrentVolume);
               mPrefs.edit().putInt("TVolume",mCurrentVolume).commit();
            }

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}

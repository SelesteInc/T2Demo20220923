//package com.weseeing.t2demo.ui;
//
//import android.app.PictureInPictureParams;
//import android.content.Context;
//import android.content.Intent;
//import android.content.res.Configuration;
//import android.os.Build;
//import android.os.Bundle;
//import android.os.Handler;
//import android.os.Looper;
//import android.os.Message;
//import android.text.TextUtils;
//import android.util.Rational;
//import android.view.Menu;
//import android.view.MenuItem;
//import android.view.TextureView;
//import android.view.Window;
//import android.view.WindowManager;
//import android.widget.Toast;
//
//import androidx.appcompat.app.AppCompatActivity;
//
//import com.pedro.vlc.VlcListener;
//import com.pedro.vlc.VlcVideoLibrary;
//import com.weseeing.framework.SeeDeviceChannel;
//import com.weseeing.framework.SeeDeviceChannelCallback;
//import com.weseeing.t2demo.R;
//import com.weseeing.t2demo.ui.widget.ViewUtils;
//
//public class StreamActivity extends AppCompatActivity implements SeeDeviceChannelCallback {
//
//    private TextureView mTv;
//
//    private String mUrl;
//
//    /**
//     * 设备通信 Device communication
//     */
//    private SeeDeviceChannel mSeeDevice;
//
//    /**
//     *视频播放 video playback
//     */
//    private VlcVideoLibrary mVlc;
//
//
//    // UI线程的Handler Handler for UI thread
//    Handler mHandler = new Handler(Looper.getMainLooper()) {
//        @Override
//        public void handleMessage(Message msg) {
//            super.handleMessage(msg);
//        }
//    };
//
//    public static void Launcher(Context context, String url){
//        Intent intent = new Intent(context,StreamActivity.class);
//        intent.putExtra("url",url);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(Bundle savedInstanceState) {
//        requestWindowFeature(Window.FEATURE_NO_TITLE);
//        getWindow().setFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON,
//                WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_stream);
//        initData(savedInstanceState);
//        findView();
//        ViewUtils.createProgressDialog(this, "正在加载中...", getResources().getColor(R.color.colorPrimary)).show(); // Loading
//        runUIRunnable(new Runnable() {
//            @Override
//            public void run() {
//                startPlay();
//            }
//        },2000);
//
//    }
//
//    private void findView(){
//        mTv = (TextureView)findViewById(R.id.textureView);
//    }
//
//    private void initData(Bundle savedInstanceState ){
//        getSupportActionBar().setDisplayShowHomeEnabled(false);
//        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
//
//        mUrl = savedInstanceState == null ? getIntent().getStringExtra("url")
//                : savedInstanceState.getString("url");
//        if (TextUtils.isEmpty(mUrl)){
//            finish();
//            return;
//        }
//
//        //设备通信管理 Device Communication Management
//        mSeeDevice = SeeDeviceChannel.create(getApplication(), SeeDeviceChannel.TYPE_WIFI_DEVICE,null);
//        mSeeDevice.sendCmdData(T2TestData.getTestData(this,0x0017,""));
//    }
//
//
//    @Override
//    public void onPictureInPictureModeChanged(boolean isInPictureInPictureMode, Configuration newConfig) {
//        super.onPictureInPictureModeChanged(isInPictureInPictureMode, newConfig);
//        if (isInPictureInPictureMode) {
//            getSupportActionBar().hide();
//        } else {
//            getSupportActionBar().show();
//        }
//    }
//
//
//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        // Inflate the menu; this adds items to the action bar if it is present.
//        super.onCreateOptionsMenu(menu);
//        getMenuInflater().inflate(R.menu.stream, menu);
//        return true;
//    }
//
//    @Override
//    public boolean onOptionsItemSelected(MenuItem item) {
//        // Handle action bar item clicks here. The action bar will
//        // automatically handle clicks on the Home/Up button, so long
//        // as you specify a parent activity in AndroidManifest.xml.
//        int id = item.getItemId();
//        //noinspection SimplifiableIfStatement
//        if (id == R.id.bgStream) {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
//                PictureInPictureParams.Builder mPictureInPictureParamsBuilder =
//                        new PictureInPictureParams.Builder();
//                mPictureInPictureParamsBuilder.setAspectRatio(new Rational(mTv.getWidth(), mTv.getHeight())).build();
//                enterPictureInPictureMode(mPictureInPictureParamsBuilder.build());
//            } else {
//                Toast.makeText(getApplication(), "你的Android版本不支持!", Toast.LENGTH_LONG).show(); // Your Android version is not supported!
//            }
//            return true;
//        } else if (android.R.id.home==id) {
//            finish();
//            return true;
//        }
//        return super.onOptionsItemSelected(item);
//    }
//
//    @Override
//    protected void onSaveInstanceState(Bundle outState) {
//        super.onSaveInstanceState(outState);
//        outState.putString("url", mUrl);
//    }
//
//    @Override
//    public void onReceive(Object o) {
//
//    }
//
//    private void startPlay(){
//        mVlc = new VlcVideoLibrary(this,mVlcListener,mTv);
//        mVlc.play(mUrl);
//    }
//
//    private VlcListener mVlcListener = new VlcListener() {
//        @Override
//        public void onComplete() {
//            Toast.makeText(getApplication(), "Loading video...", Toast.LENGTH_LONG).show();
//            ViewUtils.dismissProgressDialog();
//        }
//
//        @Override
//        public void onError() {
//            Toast.makeText(getApplication(), "video err...", Toast.LENGTH_LONG).show();
//            //mVlc.stop();
//            ViewUtils.dismissProgressDialog();
//        }
//    };
//
//    @Override
//    protected void onDestroy() {
//        getWindow().clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON);
//        super.onDestroy();
//        mVlc.stop();
//        mSeeDevice.sendCmdData(T2TestData.getTestData(this,0x0018,""));
//        ViewUtils.dismissProgressDialog();
//    }
//
//    public void runUIRunnable(Runnable runnable, long delay) {
//        if (delay > 0) {
//            mHandler.removeCallbacks(runnable);
//            mHandler.postDelayed(runnable, delay);
//        }
//        else {
//            mHandler.post(runnable);
//        }
//    }
//}

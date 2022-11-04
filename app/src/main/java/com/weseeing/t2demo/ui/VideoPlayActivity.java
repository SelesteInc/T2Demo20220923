package com.weseeing.t2demo.ui;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.MediaController;
import android.widget.VideoView;
import com.weseeing.t2demo.R;

public class VideoPlayActivity extends AppCompatActivity {


    private VideoView mVideoView;
    private  String mUrl;
    private  String mName;

    public static void launch(Activity from, String name,String url) {
        Intent intent  = new Intent(from, VideoPlayActivity.class);
        intent.putExtra("name", name);
        intent.putExtra("url", url);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_video);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        mVideoView = findViewById(R.id.videoView);

        mUrl = savedInstanceState == null ? getIntent().getStringExtra("url")
                : savedInstanceState.getString("url");
        mName = savedInstanceState == null ? getIntent().getStringExtra("name")
                : savedInstanceState.getString("name");
        if (!TextUtils.isEmpty(mName)){
            setTitle(mName);
        }

        initVideo();
    }

    private void initVideo(){
        if (TextUtils.isEmpty(mUrl)){
            finish();
            return;
        }

        Uri videoUri = Uri.parse(mUrl);
        mVideoView.setMediaController(new MediaController(this));
        mVideoView.setVideoURI(videoUri);
        mVideoView.requestFocus();
        mVideoView.start();
    }


    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("url", mUrl);
        outState.putString("name", mName);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch(item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}

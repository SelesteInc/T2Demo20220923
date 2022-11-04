package com.weseeing.t2demo.ui;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.appcompat.app.AppCompatActivity;

import com.bumptech.glide.Glide;
import com.weseeing.t2demo.R;
import com.weseeing.t2demo.ui.widget.ZoomImageView;


public class PhotosActivity extends AppCompatActivity {

    private ZoomImageView imageView;
    private String mBean;

    public static void launch(Activity from, String path) {
        Intent intent  = new Intent(from, PhotosActivity.class);
        intent.putExtra("bean", path);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_photos);

        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        imageView = findViewById(R.id.imageView);
        mBean = savedInstanceState == null ? getIntent().getStringExtra("bean")
                :  savedInstanceState.getString("bean");
        loadImageView();
        Log.d("TESTINGTAG","PhotosActivity instance variable: " + mBean);

        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent();
                intent.putExtra("name", mBean);
                setResult(RESULT_OK,intent);
                finish();
            }
        });
    }

    private void loadImageView(){
        if(TextUtils.isEmpty(mBean)
                || null == imageView){
            return;
        }
        imageView.reSetState();
        Glide.with(getApplication()).load(mBean).into(imageView);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        super.onSaveInstanceState(outState);
        outState.putString("bean", mBean);
    }

    @Override
    protected  void onDestroy() {
        Intent intent = new Intent();
        intent.putExtra("name", mBean);
        setResult(RESULT_OK,intent);
        super.onDestroy();
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
    protected void onPause() {
        super.onPause();
    }
}

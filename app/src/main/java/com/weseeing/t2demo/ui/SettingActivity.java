package com.weseeing.t2demo.ui;


import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.appcompat.app.AppCompatActivity;

import com.weseeing.t2demo.R;
import com.weseeing.t2demo.ui.fragment.TGlassesSettingFragment;

public class SettingActivity extends AppCompatActivity {

    public static void launch(Activity from) {
        Intent intent  = new Intent(from, SettingActivity.class);
        from.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setting);
        getSupportActionBar().setDisplayShowHomeEnabled(false);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        setTitle("设置"); // set up
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                getFragmentManager().beginTransaction().replace(R.id.content_main,
                        TGlassesSettingFragment.newInstance(), "SettingFragment").commit();
            }
        });

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

}

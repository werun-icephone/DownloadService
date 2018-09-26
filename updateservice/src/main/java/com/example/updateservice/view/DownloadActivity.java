package com.example.updateservice.view;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.example.updateservice.R;
import com.example.updateservice.event.UpdateEvent;

public class DownloadActivity extends BaseActivity {

    private ProgressBar pb_bar;
    private TextView tv_progress;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_download_progress_bar);

        pb_bar = findViewById(R.id.pb_progressBar_bar);
        tv_progress = findViewById(R.id.tv_progressBar_progress);

        registerDownloadingBroadcast();
        registerDownloadEndBroadcast();
    }


    private void updateProgress(int progress){

        String progressStr= progress + "%";

        if(pb_bar != null){
            tv_progress.setText(progressStr);
            pb_bar.setProgress(progress);
        }

        if(progress >= 100){
            finish();
        }

    }

    private void registerDownloadEndBroadcast(){
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateEvent.UPDATE_BROADCAST_DOWNLOAD_END_ACTION);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                finish();
            }
        };

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,intentFilter);
    }

    private void registerDownloadingBroadcast(){
        LocalBroadcastManager localBroadcastManager = LocalBroadcastManager.getInstance(this);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(UpdateEvent.UPDATE_BROADCAST_DOWNLOADING_ACTION);

        BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                int progress = intent.getIntExtra(UpdateEvent.UPDATE_PROGRESS_VALUE,0);
                updateProgress(progress);
            }
        };

        //将广播进行注册
        localBroadcastManager.registerReceiver(broadcastReceiver,intentFilter);
    }

}

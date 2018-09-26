package com.example.updateservice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.updateservice.R;
import com.example.updateservice.event.UpdateEvent;
import com.example.updateservice.util.InstallUtil;

public class InstallDialogActivity extends BaseActivity implements View.OnClickListener {

    private TextView tv_title;
    private TextView tv_content;

    private Button btn_accept;
    private Button btn_cancel;

    private String apkPath;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);

        receiveExtra();

        tv_content = findViewById(R.id.tv_dialog_content);
        tv_title = findViewById(R.id.tv_dialog_title);

        btn_accept = findViewById(R.id.btn_dialog_accept);
        btn_cancel = findViewById(R.id.btn_dialog_cancel);

        init();
    }

    private void receiveExtra(){
        apkPath = getIntent().getStringExtra(UpdateEvent.UPDATE_FILE_PATH_VALUE);
    }

    private void init(){
        btn_accept.setText(getString(R.string.install_btn_accept));
        btn_cancel.setText(getString(R.string.install_btn_cancel));

        tv_content.setText(getString(R.string.install_download_complete));
        tv_title.setText(getString(R.string.install_title));

        btn_cancel.setOnClickListener(this);
        btn_accept.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_dialog_accept) {
            InstallUtil.installApk(this,apkPath);
        }

        finish();
    }
}

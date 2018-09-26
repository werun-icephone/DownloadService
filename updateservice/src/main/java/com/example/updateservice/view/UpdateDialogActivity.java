package com.example.updateservice.view;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.updateservice.R;
import com.example.updateservice.UpdateManager;
import com.example.updateservice.model.UpdateMessage;
import com.example.updateservice.event.UpdateEvent;
import com.example.updateservice.service.UpdateService;
import com.example.updateservice.model.type.UpdateType;
import com.example.updateservice.util.IntentUtil;

public class UpdateDialogActivity extends BaseActivity implements View.OnClickListener {
    
    private Button btn_ok;
    private Button btn_cancel;
    
    private TextView tv_content;

    private UpdateMessage message;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_update_dialog);

        btn_cancel = findViewById(R.id.btn_dialog_cancel);
        btn_ok  = findViewById(R.id.btn_dialog_accept);
        tv_content = findViewById(R.id.tv_dialog_content);

        init();
    }

    private void receiveUpdateMessage(){
        message = (UpdateMessage) getIntent().getSerializableExtra(UpdateEvent.UPDATE_MESSAGE);
    }
    
    private void init(){
        receiveUpdateMessage();

        btn_ok.setOnClickListener(this);
        btn_cancel.setOnClickListener(this);

        tv_content.setText(message.getVersionDescription());
    }


    @Override
    public void onClick(View v) {
        int i = v.getId();
        if (i == R.id.btn_dialog_accept) {
            if(message== null){
                receiveUpdateMessage();
            }
            IntentUtil.startServer(this, UpdateService.class,message);
        }else if(i == R.id.btn_dialog_cancel){
            finishActivity();
        }

        finish();
    }

    private void finishActivity() {
        if (message.getUpdateType() == UpdateType.ForceUpdate) {
            forceFinishApplication();
        } else {
            finish();
        }

    }

    /**
     * 强制退出应用
     */
    private void forceFinishApplication(){
        if(UpdateManager.getInstance().getForceUpdateListener() != null){
            UpdateManager.getInstance().getForceUpdateListener().onCancel();
        }

        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {

        if(keyCode == KeyEvent.KEYCODE_BACK){
            finishActivity();
        }
        return super.onKeyDown(keyCode, event);
    }
}

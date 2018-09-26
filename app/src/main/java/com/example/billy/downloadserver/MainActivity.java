package com.example.billy.downloadserver;


import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioGroup;

import com.example.updateserver.UpdateHelper;
import com.example.updateserver.model.UpdateMessage;
import com.example.updateserver.callback.ForceUpdateListener;
import com.example.updateserver.model.type.InstallType;
import com.example.updateserver.model.type.UpdateMode;
import com.example.updateserver.model.type.UpdateType;


public class MainActivity extends AppCompatActivity implements RadioGroup.OnCheckedChangeListener {

    private RadioGroup rg_updateType;
    private RadioGroup rg_updateMode;
    private RadioGroup rg_installType;

    private EditText ed_version;

    private Button btn_startUpdate;

    private boolean autoUpdate = false;
    private boolean autoUpdateInWifi = false;
    private boolean normalUpdate = false;

    private boolean silent = false;
    private boolean active = false;

    private boolean automaticInstall = false;
    private boolean nonautomaticInstall = false;

    private UpdateMode updateMode = UpdateMode.Active;
    private UpdateType updateType = UpdateType.AutoUpdate;
    private InstallType installType = InstallType.AutomaticInstall;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        btn_startUpdate = findViewById(R.id.btn_download);

        rg_installType = findViewById(R.id.rg_demo_install_type);
        rg_updateMode = findViewById(R.id.rg_demo_update_mode);
        rg_updateType = findViewById(R.id.rg_demo_update_type);

        ed_version = findViewById(R.id.ed_demo_version_code);

        init();

    }

    private void init(){
        btn_startUpdate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String versionStr = ed_version.getText().toString();
                int versionCode = Integer.parseInt(versionStr);

                checkValue();
                download(versionCode);
            }
        });

        rg_updateType.setOnCheckedChangeListener(this);
        rg_updateMode.setOnCheckedChangeListener(this);
        rg_installType.setOnCheckedChangeListener(this);

    }

    private void download(int versionCode) {


        UpdateMessage message =
                new UpdateMessage.Builder("https://aq.qq.com/cn2/manage/mbtoken/mbtoken_download?Android=1&flow_id=1007", versionCode)
                        .setUpdateType(updateType)    //升级方式(自动更新,手动更新,强制更新)(默认自动更新)
                        .setUpdateMode(updateMode)    //下载安装包的模式(前台下载,后台下载)(默认后台下载)
                        .setInstallType(installType)  //安装安装包的模式(自动安装,询问用户后安装)(默认自动安装)
                        .build();

        new UpdateHelper(message)
                .setForceUpdateListener(new ForceUpdateListener() {
                    @Override
                    public void onCancel() {
                        finish();
                    }
                })                                            //强制更新的接口(不是必须要实现)
                .startUpdateMission(this);          //开始更新任务
    }

    private void checkValue(){
        if(autoUpdate){
            updateType = UpdateType.AutoUpdate;
        }

        if(autoUpdateInWifi){
            updateType = UpdateType.AutoUpdateInWifi;
        }

        if(normalUpdate){
            updateType = UpdateType.NormalUpdate;
        }

        if(active){
            updateMode = UpdateMode.Active;
        }

        if(silent){
            updateMode = UpdateMode.Silent;
        }

        if(automaticInstall){
            installType = InstallType.AutomaticInstall;
        }

        if(nonautomaticInstall){
            installType = InstallType.NonAutomaticInstall;
        }
    }


    @Override
    public void onCheckedChanged(RadioGroup group, int checkedId) {
        switch (group.getId()){
            case R.id.rg_demo_install_type:
                installTypeGroupChoose(checkedId);
                break;
            case R.id.rg_demo_update_mode:
                updateModeGroupChoose(checkedId);
                break;
            case R.id.rg_demo_update_type:
                updateTypeGroupChoose(checkedId);
                break;
        }
    }

    private void installTypeGroupChoose(int id){
        cleanInstallTypeChoose();
        switch (id){
            case R.id.rb_demo_auto:
                automaticInstall = true;
                break;
            case R.id.rb_demo_nonauto:
                nonautomaticInstall = true;
                break;
        }
    }

    private void cleanInstallTypeChoose(){
        automaticInstall  = false;
        nonautomaticInstall = false;
    }

    private void updateTypeGroupChoose(int id){
        cleanUpdateTypeChoose();
        switch (id){
            case R.id.rb_demo_auto:
                autoUpdate = true;
                break;
            case R.id.rb_demo_auto_update_in_wifi:
                autoUpdateInWifi = true;
                break;
            case R.id.rb_demo_normal_update:
                normalUpdate = true;
                break;
            case R.id.rb_demo_force_update:
                updateType = UpdateType.ForceUpdate;
                break;
        }
    }

    private void cleanUpdateTypeChoose(){
        autoUpdateInWifi = false;
        autoUpdate = false;
        normalUpdate = false;
    }

    private void updateModeGroupChoose(int id){
        cleanUpdateModeChoose();
        switch (id){
            case R.id.rb_demo_active:
                active = true;
                break;
            case R.id.rb_demo_silent:
                silent = true;
                break;
        }
    }

    private void cleanUpdateModeChoose(){
        active = false;
        silent = false;
    }



}

package com.example.updateservice;

import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;

import com.example.updateservice.model.UpdateMessage;
import com.example.updateservice.callback.ForceUpdateListener;
import com.example.updateservice.callback.ICheckIsNeedUpdate;
import com.example.updateservice.event.UpdateEvent;
import com.example.updateservice.network.broadcast.OnConnectedStateChangeListener;
import com.example.updateservice.network.broadcast.NetworkBroadcast;
import com.example.updateservice.service.UpdateService;
import com.example.updateservice.view.UpdateDialogActivity;


/**
 * Created by Billy on 2018/4/14.
 *p
 * 升级操作
 */

public class UpdateManager {

    private UpdateMessage message = null;

    private ForceUpdateListener mForceUpdateListener;
    private ICheckIsNeedUpdate mCheckIsNeedUpdate;

    private UpdateManager(){
    }

    public static UpdateManager getInstance() {
        return UpdateManagerHolder.updateManager;
    }

    public ForceUpdateListener getForceUpdateListener() {
        return mForceUpdateListener;
    }

    public void setCheckIsNeedUpdateInterface(ICheckIsNeedUpdate checkIsNeedUpdateInterface){
        this.mCheckIsNeedUpdate = checkIsNeedUpdateInterface;
    }

    public void setForceUpdateListener(ForceUpdateListener mForceUpdateListener) {
        this.mForceUpdateListener = mForceUpdateListener;
    }

    public UpdateManager setUpdateMessage(UpdateMessage message){
        this.message = message;
        return this;
    }

    public void executeMission(Context context) {
        if (message == null) {
            throw new NullPointerException("haven't found updateMessage");
        }

        executeByUpdateType(context);
    }


    public ICheckIsNeedUpdate getCheckIsNeedUpdate() {
        return mCheckIsNeedUpdate;
    }

    private void executeByUpdateType(Context context){
        switch (message.getUpdateType()){
            case AutoUpdate:
                autoUpdate(context);
                break;
            case NormalUpdate:
                startDialogActivity(context);
                break;
            case AutoUpdateInWifi:
                autoUpdateInWifi(context);
                break;
            case ForceUpdate:
                startDialogActivity(context);
                break;
        }
    }

    private void autoUpdate(Context context){
        startUpdate(context);
    }

    private void autoUpdateInWifi(final Context context){

        final NetworkBroadcast networkBroadcast = new NetworkBroadcast();
        networkBroadcast.setOnConnectedStateChangeListener(new OnConnectedStateChangeListener() {
            @Override
            public void onWifiConnected() {
                startUpdate(context);
                context.unregisterReceiver(networkBroadcast);
            }

            @Override
            public void onMobileNetworkConnected() {

            }

            @Override
            public void onNothingConnected() {

            }
        });

        IntentFilter intentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        context.registerReceiver(networkBroadcast,intentFilter);

    }

    private void startDialogActivity(Context context){
        Intent intent = new Intent(context, UpdateDialogActivity.class);
        intent.putExtra(UpdateEvent.UPDATE_MESSAGE,message);
        context.startActivity(intent);
    }

    private void startUpdate(Context context){
        Intent intent = new Intent(context, UpdateService.class);
        intent.putExtra(UpdateEvent.UPDATE_MESSAGE,message);
        context.startService(intent);
    }


    private static class UpdateManagerHolder{
        static final UpdateManager updateManager = new UpdateManager();
    }

}

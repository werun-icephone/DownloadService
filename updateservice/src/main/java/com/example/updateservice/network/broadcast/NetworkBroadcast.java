package com.example.updateservice.network.broadcast;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo.State;

public class NetworkBroadcast extends BroadcastReceiver {

    private OnConnectedStateChangeListener onConnectedStateChangeListener;

    public void setOnConnectedStateChangeListener(OnConnectedStateChangeListener onConnectedStateChangeListener) {
        this.onConnectedStateChangeListener = onConnectedStateChangeListener;
    }

    @Override
    public void onReceive(Context context, Intent intent) {

        if(onConnectedStateChangeListener == null){
            return;
        }

        State wifiState = null;
        State mobileState =  null;

        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if(connectivityManager == null){
            return;
        }
        wifiState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState();
        mobileState = connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState();

        if(wifiState == null || mobileState == null){
            return;
        }

        if(State.CONNECTED != wifiState &&
                State.CONNECTED == mobileState){
            //手机网络连接成功
            onConnectedStateChangeListener.onMobileNetworkConnected();
        }

        else if(State.CONNECTED != wifiState
                && State.CONNECTED != mobileState)
        {
            //手机没有网络连接
            onConnectedStateChangeListener.onNothingConnected();
        }

        else if(State.CONNECTED == wifiState &&
                State.CONNECTED != mobileState)
        {
            //手机连接WiFi成功
            onConnectedStateChangeListener.onWifiConnected();
        }
    }
}

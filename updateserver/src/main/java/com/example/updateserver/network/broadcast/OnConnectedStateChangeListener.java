package com.example.updateserver.network.broadcast;

public interface OnConnectedStateChangeListener {

    void onWifiConnected();
    void onMobileNetworkConnected();
    void onNothingConnected();

}

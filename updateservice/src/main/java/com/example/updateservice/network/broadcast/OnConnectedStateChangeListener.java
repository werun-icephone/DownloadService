package com.example.updateservice.network.broadcast;

public interface OnConnectedStateChangeListener {

    void onWifiConnected();
    void onMobileNetworkConnected();
    void onNothingConnected();

}

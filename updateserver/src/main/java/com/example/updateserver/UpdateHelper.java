package com.example.updateserver;

import android.content.Context;

import com.example.updateserver.model.UpdateMessage;
import com.example.updateserver.callback.ForceUpdateListener;
import com.example.updateserver.callback.ICheckIsNeedUpdate;

public class UpdateHelper {

    private UpdateMessage message;


    public UpdateHelper(UpdateMessage message){
        this.message = message;
    }

    public UpdateHelper setForceUpdateListener(ForceUpdateListener listener){
        UpdateManager.getInstance()
                .setForceUpdateListener(listener);

        return this;
    }

    public UpdateHelper setCheckIsNeedUpdate(ICheckIsNeedUpdate checkIsNeedUpdate){
        UpdateManager.getInstance()
                .setCheckIsNeedUpdateInterface(checkIsNeedUpdate);
        return this;
    }

    public void startUpdateMission(Context context){
        UpdateManager.getInstance()
                .setUpdateMessage(message)
                .executeMission(context);
    }

}

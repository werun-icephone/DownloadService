package com.example.updateserver.network.handler;


import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.example.updateserver.model.ErrorMessage;
import com.example.updateserver.callback.UpdateListener;
import com.example.updateserver.event.DownloadEvent;

import java.io.File;


public class DownloadRequestHandler {

    private Handler mHandler;
    private UpdateListener updateListener;

    public DownloadRequestHandler(UpdateListener updateListener){
        mHandler = new Handler(Looper.getMainLooper()){
            @Override
            public void handleMessage(Message msg) {
                handleDownloadMessage(msg);
            }
        };

        this.updateListener = updateListener;
    }


    private void handleDownloadMessage(Message msg){

        Object obj = msg.obj;

        switch (msg.what) {
            case DownloadEvent.DOWNLOAD_SUCCESS:
                updateListener.onFinish((File)obj);
                break;
            case DownloadEvent.DOWNLOAD_FAILURE:
                ErrorMessage  message = (ErrorMessage) obj;
                updateListener.onFailure(message.getCompleteSize(),message.getErrorType().toString());
                break;
            case DownloadEvent.DOWNLOAD_START:
                updateListener.onStart();
                break;
            case DownloadEvent.DOWNLOADING:
                updateListener.onUpdate((int)obj);
                break;
        }
    }

    /**
     * 发送信息
     * @param what 发送的信息代号
     * @param obj  发送的信息对象
     */
    public void sendMessage(int what,Object obj){

        Message msg = obtainMessage(what,obj);

        if(mHandler != null){
            mHandler.sendMessage(msg);
        }else{
            handleDownloadMessage(msg);
        }
    }


    private Message obtainMessage(int what,Object obj){
        Message msg;
        if(mHandler != null){
            msg = mHandler.obtainMessage(what,obj);
        }else{
            msg = Message.obtain();
            msg.what = what;
            msg.obj = obj;
        }

        return msg;
    }
}

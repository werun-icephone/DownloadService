package com.example.updateservice.network;

import android.text.TextUtils;

import com.example.updateservice.model.ErrorMessage;
import com.example.updateservice.callback.FileCallback;
import com.example.updateservice.callback.UpdateListener;
import com.example.updateservice.event.DownloadEvent;
import com.example.updateservice.network.handler.DownloadRequestHandler;
import com.example.updateservice.model.type.ErrorType;

import java.io.File;

import okhttp3.Call;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class DownloadManager {

    private OkHttpClient client;
    private Call mCall;

    private DownloadManager(){
        client = new OkHttpClient();
    }

    public static DownloadManager getInstance(){
        return DownloadManagerHolder.downloadManager;
    }

    public void cancel(){
        if(mCall != null){
            mCall.cancel();
        }
    }

    private Call newCall(String url){
        Request request = new Request.Builder()
                .url(url)
                .build();

        return client.newCall(request);
    }

    public void startDownload(String downloadUrl, String dirPath, String apkName, UpdateListener updateListener){

        final DownloadRequestHandler handler = new DownloadRequestHandler(updateListener);

        if(downloadUrl != null && !TextUtils.isEmpty(downloadUrl)) {

            handler.sendMessage(DownloadEvent.DOWNLOAD_START,null);

            mCall = newCall(downloadUrl);
            mCall.enqueue(new FileCallback(dirPath,apkName) {
                @Override
                public void onSuccess(File apk, Call call, Response response) {
                    handler.sendMessage(DownloadEvent.DOWNLOAD_SUCCESS,apk);
                }

                @Override
                public void onFailure(long completeSize, ErrorType errorType) {

                    ErrorMessage message = new ErrorMessage();
                    message.setCompleteSize(completeSize);
                    message.setErrorType(errorType);

                    handler.sendMessage(DownloadEvent.DOWNLOAD_FAILURE,message);
                }

                @Override
                public void onDownloading(int progress) {
                    handler.sendMessage(DownloadEvent.DOWNLOADING,progress);
                }
            });


        }
    }

    private static class DownloadManagerHolder{
        static DownloadManager downloadManager = new DownloadManager();
    }



}

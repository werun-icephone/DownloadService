package com.example.updateservice.callback;

import android.os.Handler;
import android.os.Looper;
import android.support.annotation.NonNull;
import android.util.Log;

import com.example.updateservice.model.type.ErrorType;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.DecimalFormat;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

public abstract class FileCallback implements Callback {

    private String path;
    private String name;

    private Handler mHandler;

    private long mCompleteSize = 0;

    protected FileCallback(String path, String name){
        this.path = path;
        this.name = name;

        mHandler = new Handler(Looper.getMainLooper());
    }

    @Override
    public void onFailure(@NonNull Call call, @NonNull IOException e) {
        mHandler.post(new Runnable() {
            @Override
            public void run() {
                onFailure(mCompleteSize,ErrorType.NetworkError);
            }
        });
    }

    @Override
    public void onResponse(@NonNull final Call call, @NonNull final Response response) {

        InputStream inputStream = null;
        byte[] buff = new byte[1024];
        int length;
        FileOutputStream fileOutputStream = null;

        //文件要储存的位置
        File dir = new File(path);
        if(!dir.exists()){
            boolean isSuccess = dir.mkdirs();
            if(!isSuccess)
                Log.d("FileCallback", "onResponse: mkdirs not success" );
            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onFailure(mCompleteSize,ErrorType.DirectoryNoFound);
                }
            });
        }

        try {

            inputStream = response.body().byteStream();
            long size = response.body().contentLength();

            final File apk = new File(path,name);
            if(apk.exists()){
               boolean result = apk.delete();
               if(!result)
                   Log.d("FileCallback", "onResponse: delete file failure");
            }else{
                boolean result =apk.createNewFile();
                if(!result){
                    Log.d("FileCallback", "onResponse: apk create failure");
                    onFailure(mCompleteSize,ErrorType.ApkCreateError);
                }

            }

            fileOutputStream = new FileOutputStream(apk);


            int progress;
            int preProgress = 0;

            while ((length = inputStream.read(buff)) != -1) {
                fileOutputStream.write(buff, 0, length);
                mCompleteSize += length;

                progress = (int) Float.parseFloat(getTwoPointFloatString(((float)mCompleteSize / size) * 100));
                if (preProgress != progress && progress <= 100) {
                    final int finalProgress = progress;
                    mHandler.post(new Runnable() {
                        @Override
                        public void run() {
                            onDownloading(finalProgress);
                        }
                    });
                }

                preProgress = progress;
            }

            fileOutputStream.flush();

            mHandler.post(new Runnable() {
                @Override
                public void run() {
                    onSuccess(apk,call,response);
                }
            });

        }catch (NullPointerException | IOException e){
            e.printStackTrace();
        }finally {
            try {
                if (inputStream != null) {
                    inputStream.close();
                }

                if (fileOutputStream != null) {
                    fileOutputStream.close();
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }


    }

    /**
     * 格式化数字
     * @param value 浮点型数字
     * @return  两位小数的浮点型数字的字符串
     */
    private String getTwoPointFloatString(float value){
        DecimalFormat format = new DecimalFormat("0.00");
        return format.format(value);
    }

    public abstract void onSuccess(File apk,Call call,Response response);
    public abstract void onFailure(long completeSize, ErrorType errorType);
    public abstract void onDownloading(int progress);
}

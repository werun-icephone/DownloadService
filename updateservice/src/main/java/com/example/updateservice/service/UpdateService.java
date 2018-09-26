package com.example.updateservice.service;


import android.app.Notification;
import android.app.NotificationManager;
import android.app.Service;

import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.os.IBinder;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.text.TextUtils;
import android.util.Log;
import android.widget.RemoteViews;

import com.example.updateservice.UpdateManager;
import com.example.updateservice.model.UpdateMessage;
import com.example.updateservice.callback.UpdateListener;
import com.example.updateservice.event.NotificationEvent;
import com.example.updateservice.event.UpdateEvent;
import com.example.updateservice.network.DownloadManager;
import com.example.updateservice.model.type.InstallType;
import com.example.updateservice.model.type.UpdateMode;
import com.example.updateservice.util.InstallUtil;
import com.example.updateservice.R;
import com.example.updateservice.view.DownloadActivity;
import com.example.updateservice.view.InstallDialogActivity;

import java.io.File;

/**
 * Created by Billy on 2018/4/15.
 *
 * 升级的服务
 */

public class UpdateService extends Service {

    private RemoteViews contentView;

    private Notification mNotification;
    private NotificationManager notificationManager;

    private String dirPath;
    private String apkName;
    private String apkPath;

    private UpdateMessage message;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        dirPath = InstallUtil.getDirectoryPath();
        apkName = InstallUtil.getApkName(this);

        createNotification();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {

        if (intent != null) {
            message = (UpdateMessage) intent.getSerializableExtra(UpdateEvent.UPDATE_MESSAGE);
            boolean isNeedInstall = configUpdateServer(message);

            if(isNeedInstall){
                startDownload(message.getDownloadUrl());
            }else{
                stopSelf();
            }

        } else {
            stopSelf();
        }

        return super.onStartCommand(intent, flags, startId);
    }

    private void startDownload(String url){
        DownloadManager.getInstance().startDownload(url, dirPath, apkName, new UpdateListener() {
            @Override
            public void onStart() {
                Log.d("okHttpDownload", "onStart: download start");
            }

            @Override
            public void onUpdate(int progress) {
                Log.d("okHttpDownload", "onStart: downloading... " + progress + "%");
                notifyDownloadProgress(progress);
                broadcastDownloadProgress(progress);
            }

            @Override
            public void onFinish(File apk) {
                Log.d("okHttpDownload", "onFinish: download end");
                notifyDownloadEnd(apk);
                broadcastDownloadEnd(apk.getAbsolutePath());
                installApk(apk);
                stopSelf();
                //InstallUtil.installApk(UpdateService.this,apk);
            }

            @Override
            public void onFailure(long completeSize, String message) {
                Log.d("okHttpDownload", "onFailure: " + message);
                notifyDownloadFailure();
                broadcastDownloadFailure();
                stopSelf();
            }
        });
    }

    /**
     * 创建通知栏
     */
    private void createNotification(){
        Notification.Builder builder = new Notification.Builder(this);
        builder.setSmallIcon(getApplicationInfo().icon)
                .setLargeIcon(BitmapFactory.decodeResource(getResources(),getApplicationInfo().icon))
                .setAutoCancel(true)
                .setTicker("downloading...")
                .setWhen(System.currentTimeMillis());

        mNotification = builder.build();

        contentView = new RemoteViews(getPackageName(), R.layout.notification_update_download);
        contentView.setTextViewText(R.id.tv_download_title,getString(getApplicationInfo().labelRes));
        contentView.setProgressBar(R.id.pb_download_downloadProgress,100,0,false);
        contentView.setTextViewText(R.id.tv_download_content,getString(R.string.download_content));

        mNotification.contentView =contentView;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;


        notificationManager = (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
        if (notificationManager != null) {
            notificationManager.notify(NotificationEvent.NOTIFY_DOWNLOADING,mNotification);
        }
    }

    /**
     * 通知下载的进度
     * @param progress  下载进度
     */
    private void notifyDownloadProgress(int progress){
        contentView.setProgressBar(R.id.pb_download_downloadProgress,100,progress,false);
        if(progress <= 100) {
            contentView.setTextViewText(R.id.tv_download_content, progress + "%");
        }else{
            contentView.setTextViewText(R.id.tv_download_content,getString(R.string.download_complete));
        }

        mNotification.contentView = contentView;
        notificationManager.notify(NotificationEvent.NOTIFY_DOWNLOADING,mNotification);
    }

    /**
     * 进行下载完毕的通知
     * @param apk  下载完毕的文件
     */
    private void notifyDownloadEnd(File apk){
        contentView = new RemoteViews(getPackageName(),R.layout.notification_download_end);
        contentView.setTextViewText(R.id.tv_download_end,getString(R.string.download_end));
        contentView.setImageViewBitmap(R.id.iv_download_icon,
                BitmapFactory.decodeResource(getResources(),getApplicationInfo().icon));

        mNotification.contentView = contentView;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.tickerText = getString(R.string.download_complete);
        mNotification.contentIntent = InstallUtil.getInstallApkIntent(this,apk);

        notificationManager.cancel(NotificationEvent.NOTIFY_DOWNLOADING);
        notificationManager.notify(NotificationEvent.NOTIFY_DOWNLOAD_END,mNotification);
    }

    /**
     * 发布通知下载失败
     */
    private void notifyDownloadFailure(){
        contentView = new RemoteViews(getPackageName(),R.layout.notification_download_end);
        contentView.setTextViewText(R.id.tv_download_end,getString(R.string.download_failure));
        contentView.setImageViewBitmap(R.id.iv_download_icon,
                BitmapFactory.decodeResource(getResources(),getApplicationInfo().icon));

        mNotification.contentView = contentView;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;

        notificationManager.cancelAll();
        notificationManager.notify(NotificationEvent.NOTIFY_DOWNLOAD_FAILURE,mNotification);
    }

    /**
     * 通知apk已经存在
     * @param apk  apk文件
     */
    private void notifyApkIsExist(File apk){
        contentView = new RemoteViews(getPackageName(),R.layout.notification_download_end);
        contentView.setTextViewText(R.id.tv_download_end,getString(R.string.download_apk_exist));
        contentView.setImageViewBitmap(R.id.iv_download_icon,
                BitmapFactory.decodeResource(getResources(),getApplicationInfo().icon));

        mNotification.contentView = contentView;
        mNotification.flags = Notification.FLAG_AUTO_CANCEL;
        mNotification.tickerText = getString(R.string.download_apk_exist);
        mNotification.contentIntent = InstallUtil.getInstallApkIntent(this,apk);

        notificationManager.cancel(NotificationEvent.NOTIFY_DOWNLOADING);
        notificationManager.notify(NotificationEvent.NOTIFY_APK_EXIST,mNotification);
    }

    private boolean checkApkIsExist(String apkPath){
        File apk = new File(apkPath);

        if(!apk.exists()){
            return false;
        }

        int versionCode = InstallUtil.getVersionCode(this,apkPath);
        Log.d("download_server", "checkApkIsExist: version code = " + versionCode);
        return versionCode == message.getVersionCode();
    }

    /**
     * 配置升级服务的升级方式
     *
     * @param message  升级的信息
     *
     * @return true   需要下载安装包
     *          false  不需要下载安装包
     */
    private boolean configUpdateServer(UpdateMessage message){
        //如果有自定义的路径则使用自定义的路径
        if(!TextUtils.isEmpty(message.getLocalFilePath())){
            dirPath = message.getLocalFilePath();
        }

        apkPath = dirPath + "/" + apkName;

        //检查apk是否存在
        if(checkApkIsExist(apkPath)){
            //检查是否需要更新
            if(checkIsNeedUpdate(this)){
                //安装apk
                notifyApkIsExist(new File(apkPath));
                installApk(new File(apkPath));
                return false;
            }
        }

        if(message.getUpdateMode() == UpdateMode.Active){
            startActivity(DownloadActivity.class);
        }

        return true;
    }


    /**
     * 检查是否需要进行更新
     * @param context  上下文对象
     * @return  需要进行更新
     */
    private boolean checkIsNeedUpdate(Context context) {
        if (UpdateManager.getInstance().getCheckIsNeedUpdate() != null) {
            return UpdateManager.getInstance().getCheckIsNeedUpdate().check();
        }
        int versionCode = InstallUtil.getVersionCode(context);
        return versionCode != InstallUtil.GET_VERSION_CODE_ERROR
                && versionCode < message.getVersionCode();
    }


    /**
     * 安装apk
     * @param apk apk文件
     */
    private void installApk(File apk){
        if(message.getInstallType() == InstallType.AutomaticInstall){
            InstallUtil.installApk(this,apk);
        }else{
            startActivity(InstallDialogActivity.class);
        }
    }

    /**
     * 开启前台下载的界面
     */
    private void startActivity(Class<?> cls){
        Intent intent = new Intent(getApplicationContext(),cls);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.putExtra(UpdateEvent.UPDATE_MESSAGE,message);
        intent.putExtra(UpdateEvent.UPDATE_FILE_PATH_VALUE,apkPath);
        startActivity(intent);
    }

    /**
     * 将进度信息广播出去
     * @param progress   进度信息
     */
    private void broadcastDownloadProgress(int progress){
        Intent intent = new Intent(UpdateEvent.UPDATE_BROADCAST_DOWNLOADING_ACTION);
        intent.putExtra(UpdateEvent.UPDATE_PROGRESS_VALUE,progress);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }

    /**
     * 将下载失败的信息广播出去
     */
    private void broadcastDownloadFailure(){
        Intent intent = new Intent(UpdateEvent.UPDATE_BROADCAST_DOWNLOADING_ACTION);
        intent.putExtra(UpdateEvent.UPDATE_FAILURE,true);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }


    /**
     * 广播下载完成
     * @param path  文件的路径
     */
    private void broadcastDownloadEnd(String path){
        Intent intent = new Intent(UpdateEvent.UPDATE_BROADCAST_DOWNLOAD_END_ACTION);
        intent.putExtra(UpdateEvent.UPDATE_FILE_PATH_VALUE,path);
        LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
    }
}

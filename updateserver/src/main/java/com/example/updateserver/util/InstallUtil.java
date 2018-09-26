package com.example.updateserver.util;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Environment;
import android.util.Log;

import com.example.updateserver.UpdateManager;
import com.example.updateserver.event.DownloadEvent;

import java.io.File;

public class InstallUtil {

    private static String type = "application/vnd.android.package-archive";

    public static int GET_VERSION_CODE_ERROR = -1;

    /**
     * 安装apk
     */
    public static void installApk(Context context,File apk){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apk),type);
        context.startActivity(intent);
    }

    public static void installApk(Context context,String path){
        File file = new File(path);
        installApk(context,file);
    }

    public static PendingIntent getInstallApkIntent(Context context,File apk){
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setDataAndType(Uri.fromFile(apk),type);

        return PendingIntent.getActivity(context, DownloadEvent.DOWNLOAD_SUCCESS,intent,PendingIntent.FLAG_ONE_SHOT);
    }

    /**
     * 获取当前版本的名称
     * @param context   上下文对象
     * @return    null 获得版本名称失败
     *                  获得版本的名称
     */
    public static String getVersionName(Context context){
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionName;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return null;
    }


    /**
     * 获取当前的版本号
     * @param context   上下文对象
     * @return          当前的版本号
     */
    public static int getVersionCode(Context context){
        PackageManager packageManager = context.getPackageManager();

        try {
            PackageInfo packageInfo = packageManager.getPackageInfo(context.getPackageName(),0);
            return packageInfo.versionCode;
        } catch (PackageManager.NameNotFoundException e) {
            e.printStackTrace();
        }

        return GET_VERSION_CODE_ERROR;
    }

    /**
     * 根据apk的路径获取apk的版本号
     * @param context   上下文对象
     * @param apkPath   apk的路径
     * @return    apk的版本号
     */
    public static int getVersionCode(Context context,String apkPath){
        PackageManager packageManager = context.getPackageManager();
        PackageInfo packageInfo = packageManager.getPackageArchiveInfo(apkPath,PackageManager.GET_ACTIVITIES);
        return packageInfo.versionCode;
    }

    /**
     * 获取apk安装的文件夹所在的位置
     * @return  文件夹所在的位置
     */
    public static String getDirectoryPath(){
        return Environment.getExternalStorageDirectory().getAbsolutePath();
    }

    /**
     * 获取apk的名字
     * @param context 上下文对象
     * @return  apk的名字
     */
    public static String getApkName(Context context){
        return context.getPackageName() + ".apk";
    }
}

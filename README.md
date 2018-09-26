# DownloadService
自动更新的框架---冰峰工作室出品


### 使用时需要将`DownloadServer`这个module导入到原项目当中

### 使用示例
使用时只需要填充这两个方法的内容则可以实现自动更新
在使用,在你需要的地方调用这个代码,则会对由服务端获取的versionCode与android当前程序的versionCode进行对比,若新的versionCode比较大,
既可以进行升级操作
```
        UpdateMessage message =
                new UpdateMessage.Builder("new_apk_url", new_apk_versionCode)
                        .setUpdateType(updateType)    //升级方式(自动更新,手动更新,强制更新)(默认自动更新)
                        .setUpdateMode(updateMode)    //下载安装包的模式(前台下载,后台下载)(默认后台下载)
                        .setInstallType(installType)  //安装安装包的模式(自动安装,询问用户后安装)(默认自动安装)
                        .build();

        new UpdateHelper(message)
                .setForceUpdateListener(new ForceUpdateListener() {
                    @Override
                    public void onCancel() {
                        finish();
                    }
                })                                            //强制更新的接口(不是必须要实现)
                .startUpdateMission(this);          //开始更新任务
```

### 使用时可能会出现打包失败的问题
只需要将
```
implementation fileTree(dir: 'libs', include: ['*.jar'])
```
当中的 `implementation` 修改为 `provided`即可解决(这个问题源于包冲突)

# DownloadService
自动更新的框架---冰峰工作室出品


### 使用时需要将`DownloadServer`这个module导入到原项目当中
### 使用时可能会出现打包失败的问题
只需要将
```
implementation fileTree(dir: 'libs', include: ['*.jar'])
```
当中的 `implementation` 修改为 `provided`即可解决(这个问题源于包冲突)

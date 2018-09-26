package com.example.updateserver.model;

import android.text.TextUtils;

import com.example.updateserver.model.type.InstallType;
import com.example.updateserver.model.type.RequestType;
import com.example.updateserver.model.type.UpdateMode;
import com.example.updateserver.model.type.UpdateType;

import java.io.Serializable;

/**
 * 用于储存系统升级所需的信息
 *
 */

public class UpdateMessage implements Serializable {

    private String downloadUrl;
    private String version;
    private String versionDescription;

    private int versionCode;

    private String localFilePath;

    private RequestType requestType = RequestType.Get;
    private UpdateType updateType = UpdateType.AutoUpdate;
    private UpdateMode updateMode = UpdateMode.Silent;
    private InstallType installType = InstallType.AutomaticInstall;

    public InstallType getInstallType() {
        return installType;
    }

    public String getVersionDescription() {
        return versionDescription;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public UpdateMode getUpdateMode() {
        return updateMode;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public String getVersion() {
        return version;
    }

    public String getLocalFilePath() {
        return localFilePath;
    }

    public RequestType getRequestType() {
        return requestType;
    }

    public UpdateType getUpdateType() {
        return updateType;
    }

    private UpdateMessage(String downloadUrl, int version){
        this.downloadUrl = downloadUrl;
        this.versionCode = version;
    }

    public static class Builder{

        private String downloadUrl;
        private String version;
        private String versionDescription;

        private int versionCode;

        private String localFilePath;

        private RequestType requestType = RequestType.Get;
        private UpdateType updateType = UpdateType.AutoUpdate;
        private UpdateMode updateMode = UpdateMode.Silent;
        private InstallType installType = InstallType.AutomaticInstall;

        public Builder(String downloadUrl,int versionCode){
            this.downloadUrl = downloadUrl;
            this.versionCode = versionCode;
        }

        public Builder setInstallType(InstallType installType){
            this.installType = installType;
            return this;
        }

        public Builder setVersionDescription(String versionDescription){
            this.versionDescription = versionDescription;
            return this;
        }

        public Builder setVersion(String version){
            this.version = version;
            return this;
        }

        public Builder setUpdateType(UpdateType type){
            this.updateType = type;
            return this;
        }

        public Builder setRequestType(RequestType type){
            this.requestType = type;
            return this;
        }

        public Builder setLocalFilePath(String localFilePath){
            this.localFilePath = localFilePath;
            return this;
        }

        public Builder setUpdateMode(UpdateMode updateMode){
            this.updateMode = updateMode;
            return this;
        }

        public UpdateMessage build(){
            UpdateMessage message = new UpdateMessage(downloadUrl,versionCode);
            message.requestType = requestType;
            message.updateType = updateType;
            message.localFilePath = localFilePath;
            message.updateMode = updateMode;

            message.version = version;
            message.installType = installType;

            if(TextUtils.isEmpty(versionDescription)){
                versionDescription = versionCode + ".0.0";
            }
            message.versionDescription = versionDescription;

            return message;
        }

    }
}

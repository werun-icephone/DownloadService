package com.example.updateserver.callback;

import java.io.File;

/**
 * Created by Billy on 2018/4/14.
 *
 * 更新的接口
 */

public interface UpdateListener {

    void onStart();
    void onUpdate(int progress);
    void onFinish(File apk);
    void onFailure(long completeSize,String message);

}

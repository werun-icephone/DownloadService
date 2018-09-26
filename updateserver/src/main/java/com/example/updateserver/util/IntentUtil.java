package com.example.updateserver.util;

import android.content.Context;
import android.content.Intent;

import com.example.updateserver.model.UpdateMessage;
import com.example.updateserver.event.UpdateEvent;

public class IntentUtil {

    public static void startServer(Context context,Class<?> cls,UpdateMessage message){
        Intent intent = new Intent(context,cls);
        intent.putExtra(UpdateEvent.UPDATE_MESSAGE,message);
        context.startService(intent);
    }

    public static void startActivity(Context context, Class<?> cls, UpdateMessage message){
        Intent intent = new Intent(context,cls);
        intent.putExtra(UpdateEvent.UPDATE_MESSAGE,message);
        context.startActivity(intent);
    }

}

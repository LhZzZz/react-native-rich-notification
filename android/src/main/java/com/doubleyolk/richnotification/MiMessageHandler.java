package com.doubleyolk.richnotification;

import android.app.ActivityManager;
import android.content.Context;
import android.os.Build;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.xiaomi.mipush.sdk.MiPushClient;

import java.util.List;

public class MiMessageHandler {
    private ReactContext context;
    private String registerId;

    public MiMessageHandler(){

    }

    public MiMessageHandler(ReactContext context){
        this.context = context;
    }


    public void initService(String appId, String appKey){
        MiPushClient.registerPush(context, appId, appKey);
    }

    public void getRegisterId(Callback callback){
        registerId = MiPushClient.getRegId(context);
        WritableMap map = Arguments.createMap();
        map.putString("data",registerId);
        map.putString("brand", Build.BRAND);
        callback.invoke(map);
    }
}

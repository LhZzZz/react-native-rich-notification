package com.doubleyolk.richnotification;

import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;

public class HonorPushHandler {
    private ReactContext context;
    private String registerId;

    public HonorPushHandler(){

    }

    public HonorPushHandler(ReactContext reactContext){
        context = reactContext;
    }

    public void initService(){
        HonorPushClient.getInstance().init(context, true);
    }

    public void getRegisterId(Callback callback){
        HonorPushClient.getInstance().getPushToken(new HonorPushCallback<String>() {
            @Override
            public void onSuccess(String pushToken) {
                // TODO: 新Token处理
                Log.i("honor token",pushToken);
                WritableMap map = Arguments.createMap();
                map.putString("data",pushToken);
                map.putString("brand", Build.BRAND);
                callback.invoke(map);
            }

            @Override
            public void onFailure(int errorCode, String errorString) {
                // TODO: 错误处理
                Log.i("honor token err ",errorCode+errorString);
                WritableMap map = Arguments.createMap();
                map.putString("data","");
                map.putString("brand", Build.BRAND);
                callback.invoke(map);
            }
        });
    }
}

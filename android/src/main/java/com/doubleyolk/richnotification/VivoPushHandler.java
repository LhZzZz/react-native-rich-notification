package com.doubleyolk.richnotification;

import android.os.Build;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.vivo.push.IPushActionListener;
import com.vivo.push.PushClient;
import com.vivo.push.util.VivoPushException;

public class VivoPushHandler {
    private static final String TAG = "VivoPushHandler";
    /**
     * 注册ID
     */
    private String registerId;
    private ReactContext context;

    public VivoPushHandler(){

    }

    public VivoPushHandler(ReactContext context){
        this.context = context;
    }

    /**
     * 初始化服务
     * @throws VivoPushException
     */
    public void initService() {
        try {
            PushClient.getInstance(context).initialize();
            PushClient.getInstance(context).turnOnPush(new IPushActionListener() {
                @Override
                public void onStateChanged(int state) {
                    // TODO: 开关状态处理， 0代表成功
                }
            });
        }catch (VivoPushException e){
            Log.i(TAG,"initService failed");
        }

    }

    /**
     * 获取设备ID
     * @param callback
     */
    public void getRegisterId(Callback callback){
        String regId = PushClient.getInstance(context).getRegId();
        WritableMap map = Arguments.createMap();
        if (regId == null  || regId.isEmpty()){
            map.putString("data","");
        }else {
            map.putString("data",regId);
        }
        map.putString("brand", Build.BRAND);
        callback.invoke(map);
    }
}

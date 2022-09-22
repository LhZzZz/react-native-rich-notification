package com.doubleyolk.richnotification;


import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;
import com.facebook.react.bridge.ReactContext;

public class HwPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    private static final String MESSAGE_TAG = "RemoteMessage";
    private ReactApplicationContext context;
    private String deviceToken;
    final Messenger mMessenger= super.a;
    private ReactInstanceManager mReactInstanceManager;
    private Callback getTokenCallback;

    private final IBinder mBinder = new LocalBinder();

    public class LocalBinder extends Binder {
        public HwPushService getService() {
            // Return this instance of LocalService so clients can call public methods
            return HwPushService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    /** method for clients */
//    public int getServiceParem(){
//        return mserviceparam;
//    }


//    @Override
//    public IBinder onBind(Intent intent) {
//        return mMessenger.getBinder();
//    }


    public class IncomingHandler extends Handler {
        public void handleMessage(Message msg){
            super.handleMessage(msg);
//            Bundle bundle =  msg.getData();
//            if (bundle!=null){
//                for (String key : bundle.keySet()) {
//                    String content = bundle.getString(key);
//                    Log.i("HandleIntent", "key = " + key + ", content = " + content);
//                }
//            }
        }

    }

    public HwPushService() {

    }



    public String getDeviceToken(){
        return this.deviceToken;
    }

    public void getToken(Callback callback, ReactContext reactContext) {
        getTokenCallback = callback;
        // 创建一个新线程
        new Thread() {
            @Override
            public void run() {
                try {
                    // 从agconnect-service.json文件中读取appId
                    String appId = "your_APPId";
                    // 输入token标识"HCM"
                    String tokenScope = "HCM";
                    String token = HmsInstanceId.getInstance(reactContext).getToken(appId, tokenScope);
                    Log.i(TAG, "get token: " + token);
                    // 判断token是否为空
                    if(!TextUtils.isEmpty(token)) {
                        WritableMap map = Arguments.createMap();
                        map.putString("token",token);
                        if (getTokenCallback!=null){
                            getTokenCallback.invoke(map);
                        }
//                        sendRegTokenToServer(token);
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
    }
    private void sendRegTokenToServer(String token) {
        Log.i(TAG, "sending token to server. token:" + token);
    }

    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token);
        deviceToken = token;
        Log.i(TAG, "receive new token:" + token);
//        WritableMap map = Arguments.createMap();
//        map.putString("token",token);
//        if (getTokenCallback!=null){
//            getTokenCallback.invoke(map);
//        }
//        ReactContext context = (ReactContext) this.getApplicationContext();
//        context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
//                .emit("onNotificationTap", map);
    }

    /**
     * 收到消息的回调
     */
    @Override
    public void onMessageReceived(RemoteMessage message){
        RemoteMessage.Notification notification = message.getNotification();
        Log.i(MESSAGE_TAG, notification.getTitle());
    }

}
package com.doubleyolk.richnotification;


import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.Nullable;

import com.facebook.react.ReactInstanceManager;
import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.aaid.constant.ErrorEnum;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.HmsMessaging;
import com.huawei.hms.push.RemoteMessage;
import com.facebook.react.bridge.ReactContext;
import com.huawei.hms.push.SendException;
import com.huawei.hms.push.t;
import com.huawei.hms.support.log.HMSLog;

public class HwPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    private static final String MESSAGE_TAG = "RemoteMessage";
    private ReactContext context;
    static String deviceToken;
    private Callback getTokenCallback;
    private Messenger client;//客户端

    private final LocalBinder mBinder = new LocalBinder();
    private MyHandler myHandler = new MyHandler();
    Messenger mService = new Messenger(myHandler);


    public HwPushService() {
        super();
    }

    public class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case 100:
                    Bundle data = msg.getData();
                    String msgContent = data.getString("msg");
                    Log.i(TAG, "-----服务端收到  客户端发来的消息 建立第一次沟通："+msgContent);
                    // 1 获取客户端的messager用于发送回复它的消息    根据  Message.replyTo  获取客户端【例如activity】里的messager
                    client = msg.replyTo;
                    // 2 绑定数据
                    Bundle replyData = new Bundle();
                    //返回token
                    replyData.putString("newToken", HwPushService.this.deviceToken);
                    // 创建回复消息
                    Message replyMsg = Message.obtain();
                    replyMsg.what = HwPushHandler.MessageCode_initConnection;
                    replyMsg.setData(replyData);
                    try {
                        client.send(replyMsg);
                    } catch (RemoteException e) {
                        Log.e("flag", "", e);
                    }
                    break;
                default:super.handleMessage(msg);
            }
            if (msg == null) {
                Log.e(TAG, "receive message is null");
            } else {
                Log.i(TAG, "handle message start...");
                Bundle var3;
                if ((var3 = Message.obtain(msg).getData()) != null) {
                    Intent var2;
                    Intent var10001 = var2 = new Intent();
                    var2.putExtras(var3);
                    var10001.putExtra("inputType", var3.getInt("inputType", -1));
                    HwPushService.this.handleIntentMessage(var2);
                }

            }
        }
    }

    public class LocalBinder extends Binder {
        public HwPushService getService() {
            // Return this instance of LocalService so clients can call public methods
            return HwPushService.this;
        }
    }

    @Override
    public IBinder onBind(Intent intent) {
        return mService.getBinder();
    }

    /**
     * 获取device Token
     * @return
     */
    public String getDeviceToken(){
        return this.deviceToken;
    }

    /**
     * 重新获取token
     * @deprecated
     * @param callback
     * @param reactContext
     */
    public void getToken(Callback callback, ReactContext reactContext) {
        getTokenCallback = callback;
        if (this.deviceToken != null){
            Log.i(TAG,"has cached token");
            WritableMap map = Arguments.createMap();
            map.putString("data",deviceToken);
            map.putString("brand",Build.BRAND);
            if (getTokenCallback!=null){
                getTokenCallback.invoke(map);
            }
            return;
        }
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
                        map.putString("data",token);
                        deviceToken = token;
                        map.putString("brand",Build.BRAND);
                        if (getTokenCallback!=null){
                            getTokenCallback.invoke(map);
                        }
                    }
                } catch (ApiException e) {
                    Log.e(TAG, "get token failed, " + e);
                }
            }
        }.start();
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
     * 收到透传消息的回调
     */
    @Override
    public void onMessageReceived(RemoteMessage message){
        String data = message.getData();
        String messageId = message.getMessageId();
        Log.i(MESSAGE_TAG, data);
        //发送消息给客户端
        Bundle replyData = new Bundle();
        replyData.putString("message", data);
        replyData.putString("messageId",messageId);
        Message replyMsg = Message.obtain();
        replyMsg.what = HwPushHandler.MessageCode_onRemoteMessage;
        replyMsg.setData(replyData);
        try {
            client.send(replyMsg);
        } catch (RemoteException e) {
            Log.i(MESSAGE_TAG, "", e);
        }
    }


    /**
     * 初始化服务，主要用于获取context
     * @param context
     */
    public void initService(ReactContext context){
        this.context = context;
        setAutoInitEnabled(true);
    }

    private void setAutoInitEnabled(final boolean isEnable) {
        if(isEnable){
            // 设置自动初始化
            HmsMessaging.getInstance(this).setAutoInitEnabled(true);
        } else {
            // 禁止自动初始化
            HmsMessaging.getInstance(this).setAutoInitEnabled(false);
        }
    }



}
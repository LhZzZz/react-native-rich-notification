package com.doubleyolk.richnotification;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.huawei.hms.aaid.HmsInstanceId;
import com.huawei.hms.common.ApiException;
import com.huawei.hms.push.HmsMessaging;

import org.json.JSONObject;

public class HwPushHandler{
    private static final String TAG = "HwPushHandler";
    public static final int MessageCode_initConnection = 100;//表示与服务端第一次建立连接
    public static final int MessageCode_onNewToken = 101;//表示与服务端第一次建立连接
    public static final int MessageCode_onRemoteMessage = 102;//表示与服务端第一次建立连接

    private ReactContext context;
    private Messenger serviceMessenger;
    private Messenger ClentMessenger = new Messenger(new ClientHandler());


    private String deviceToken;
    private Callback getTokenCallback;


    public HwPushHandler(){

    }

    public HwPushHandler(ReactContext context){
        this.context = context;
    }

    public void initService(){
        Intent intent = new Intent(context, HwPushService.class);
        context.bindService(intent, new MyServiceConnection(), Context.BIND_AUTO_CREATE);
        setAutoInitEnabled(true);
    }

    /**
     * 设置角标数量
     * @param badge
     */
    public void setBadge(int badge){
        try {
            String packageName = this.context.getApplicationContext().getPackageName();
            Bundle extra = new Bundle();
            extra.putString("package", packageName);
            extra.putString("class", packageName+".MainActivity");
            extra.putInt("badgenumber", badge);
            context.getContentResolver().call(Uri.parse("content://com.huawei.android.launcher.settings/badge/"), "change_badge", null, extra);
        }catch (Exception e){

        }

    }

    /**
     *给服务端发送一条消息，告诉服务端往哪里恢复消息
     */
    private void sendReplyMessenger(){
        try {
            // 1 获取消息对象
            Message msg = Message.obtain();
            msg.what = MessageCode_initConnection;
            // 2 设置发送数据信息
            Bundle data = new Bundle();
            data.putString("msg", "客户端发来的");
            msg.setData(data);
            // 3 设置服务端回复的Messenger 【告诉服务端回复的messager  它是客户端自己创建的】
            msg.replyTo = ClentMessenger;
            // 4 使用绑定服务后的创建的 Messager发送消息
            serviceMessenger.send(msg);
        } catch (RemoteException e) {
            Log.e(TAG, "", e);
        }
    }


    private class ClientHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MessageCode_initConnection:
                    Bundle bundle = msg.getData();
                    String data = bundle.getString("newToken");
                    Log.i(TAG, "客户端收到了服务端返回的token:" + data);
                    deviceToken = data;
                    break;
                case MessageCode_onRemoteMessage:
                    handleRemoteMessage(msg);
                    break;
            }
        }
    }

    private class MyServiceConnection implements ServiceConnection {

        @Override
        public void onServiceConnected(ComponentName name, IBinder iBinder) {
            // 绑定服务后   根据绑定后返回的 IBinder 创建给 服务端发消息的  Messenger
            serviceMessenger = new Messenger(iBinder);
            sendReplyMessenger();
        }

        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e("flag", "----------onServiceDisconnected");
        }
    }

    private void setAutoInitEnabled(final boolean isEnable) {
        if(isEnable){
            // 设置自动初始化
            HmsMessaging.getInstance(this.context).setAutoInitEnabled(true);
        } else {
            // 禁止自动初始化
            HmsMessaging.getInstance(this.context).setAutoInitEnabled(false);
        }
    }


    /**
     * 返回透传消息给rn端
     * @param message
     */
    private void handleRemoteMessage(Message message){
        Bundle bundle = message.getData();
        WritableMap map = Arguments.createMap();
        for (String key : bundle.keySet()) {
            String content = bundle.getString(key);
            map.putString(key, content);
        }
        Log.i(TAG, "透传消息" +  map);
        this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class).emit("onHuaWeiRemoteMessage", map);
    }


    /**
     * 获取设备token
     * @param callback
     */
    public void getDeviceToken(Callback callback) {
        getTokenCallback = callback;
        if (this.deviceToken != null){
            Log.i(TAG,"has cached token");
            WritableMap map = Arguments.createMap();
            map.putString("data",deviceToken);
            map.putString("brand", Build.BRAND);
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
                    String token = HmsInstanceId.getInstance(context).getToken(appId, tokenScope);
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
}

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
                Log.e(TAG, "receive message is null 123123123");
            } else {
                Log.i(TAG, "handle message start... 123123123");
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

    public void handleIntentMessage(Intent var1) {
        if (var1 == null) {
            HMSLog.e("HmsMessageService", "receive message is null");
        } else {
            Intent var10000 = var1;
            Intent var10001 = var1;
            Intent var10002 = var1;
            String var10003 = "message_id";

            RuntimeException var63;
            label330: {
                Exception var62;
                label290: {
                    String var2;
                    boolean var64;
                    try {
                        var2 = var10002.getStringExtra(var10003);
                    } catch (RuntimeException var57) {
                        var63 = var57;
                        var64 = false;
                        break label330;
                    } catch (Exception var58) {
                        var62 = var58;
                        var64 = false;
                        break label290;
                    }

                    String var65 = "message_type";

                    String var3;
                    try {
                        var3 = var10001.getStringExtra(var65);
                    } catch (RuntimeException var55) {
                        var63 = var55;
                        var64 = false;
                        break label330;
                    } catch (Exception var56) {
                        var62 = var56;
                        var64 = false;
                        break label290;
                    }

                    String var67 = "transaction_id";

                    String var4;
                    try {
                        var4 = var10000.getStringExtra(var67);
                    } catch (RuntimeException var53) {
                        var63 = var53;
                        var64 = false;
                        break label330;
                    } catch (Exception var54) {
                        var62 = var54;
                        var64 = false;
                        break label290;
                    }

                    String var66 = "new_token";

                    boolean var69;
                    try {
                        var69 = var66.equals(var3);
                    } catch (RuntimeException var51) {
                        var63 = var51;
                        var64 = false;
                        break label330;
                    } catch (Exception var52) {
                        var62 = var52;
                        var64 = false;
                        break label290;
                    }

                    String var10004;
                    HmsMessageService var70;
                    if (var69) {
                        var70 = this;
                        var10001 = var1;
                        var65 = var4;
                        var10003 = "HmsMessageService";
                        var10004 = "onNewToken";

                        try {
                            HMSLog.i(var10003, var10004);
                            var70.a(var10001, var65);
                            return;
                        } catch (RuntimeException var5) {
                            var63 = var5;
                            var64 = false;
                            break label330;
                        } catch (Exception var6) {
                            var62 = var6;
                            var64 = false;
                        }
                    } else {
                        label291: {
                            var66 = "received_message";

                            try {
                                var69 = var66.equals(var3);
                            } catch (RuntimeException var49) {
                                var63 = var49;
                                var64 = false;
                                break label330;
                            } catch (Exception var50) {
                                var62 = var50;
                                var64 = false;
                                break label291;
                            }

                            String var10005;
                            HmsMessageService var68;
                            StringBuilder var72;
                            if (var69) {
                                label292: {
                                    var70 = this;
                                    var10001 = var1;
                                    var68 = this;
                                    var10003 = "HmsMessageService";

                                    try {
                                        var72 = new StringBuilder();
                                    } catch (RuntimeException var11) {
                                        var63 = var11;
                                        var64 = false;
                                        break label330;
                                    } catch (Exception var12) {
                                        var62 = var12;
                                        var64 = false;
                                        break label292;
                                    }

                                    var10005 = "onMessageReceived, message id:";

                                    try {
                                        HMSLog.i(var10003, var72.append(var10005).append(var2).toString());
                                    } catch (RuntimeException var9) {
                                        var63 = var9;
                                        var64 = false;
                                        break label330;
                                    } catch (Exception var10) {
                                        var62 = var10;
                                        var64 = false;
                                        break label292;
                                    }

                                    var10003 = "push.receiveMessage";

                                    try {
                                        var68.a(var10003, var2, ErrorEnum.SUCCESS.getInternalCode());
                                        var70.doMsgReceived(var10001);
                                        return;
                                    } catch (RuntimeException var7) {
                                        var63 = var7;
                                        var64 = false;
                                        break label330;
                                    } catch (Exception var8) {
                                        var62 = var8;
                                        var64 = false;
                                    }
                                }
                            } else {
                                label293: {
                                    var66 = "sent_message";

                                    try {
                                        var69 = var66.equals(var3);
                                    } catch (RuntimeException var47) {
                                        var63 = var47;
                                        var64 = false;
                                        break label330;
                                    } catch (Exception var48) {
                                        var62 = var48;
                                        var64 = false;
                                        break label293;
                                    }

                                    if (var69) {
                                        try {
                                            this.b(var4, var2);
                                            return;
                                        } catch (RuntimeException var13) {
                                            var63 = var13;
                                            var64 = false;
                                            break label330;
                                        } catch (Exception var14) {
                                            var62 = var14;
                                            var64 = false;
                                        }
                                    } else {
                                        label294: {
                                            var66 = "send_error";

                                            try {
                                                var69 = var66.equals(var3);
                                            } catch (RuntimeException var45) {
                                                var63 = var45;
                                                var64 = false;
                                                break label330;
                                            } catch (Exception var46) {
                                                var62 = var46;
                                                var64 = false;
                                                break label294;
                                            }

                                            Intent var73;
                                            if (var69) {
                                                var70 = this;
                                                var67 = var4;
                                                var65 = var2;
                                                var73 = var1;
                                                var10004 = "error";

                                                try {
                                                    var70.b(var67, var65, var73.getIntExtra(var10004, ErrorEnum.ERROR_UNKNOWN.getInternalCode()));
                                                    return;
                                                } catch (RuntimeException var15) {
                                                    var63 = var15;
                                                    var64 = false;
                                                    break label330;
                                                } catch (Exception var16) {
                                                    var62 = var16;
                                                    var64 = false;
                                                }
                                            } else {
                                                label295: {
                                                    var66 = "delivery";

                                                    try {
                                                        var69 = var66.equals(var3);
                                                    } catch (RuntimeException var43) {
                                                        var63 = var43;
                                                        var64 = false;
                                                        break label330;
                                                    } catch (Exception var44) {
                                                        var62 = var44;
                                                        var64 = false;
                                                        break label295;
                                                    }

                                                    if (var69) {
                                                        label296: {
                                                            var70 = this;
                                                            var67 = var2;
                                                            var68 = this;
                                                            var73 = var1;
                                                            var10004 = "error";

                                                            int var59;
                                                            try {
                                                                var59 = var73.getIntExtra(var10004, ErrorEnum.ERROR_APP_SERVER_NOT_ONLINE.getInternalCode());
                                                            } catch (RuntimeException var27) {
                                                                var63 = var27;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var28) {
                                                                var62 = var28;
                                                                var64 = false;
                                                                break label296;
                                                            }

                                                            var10003 = "HmsMessageService";

                                                            try {
                                                                var72 = new StringBuilder();
                                                            } catch (RuntimeException var25) {
                                                                var63 = var25;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var26) {
                                                                var62 = var26;
                                                                var64 = false;
                                                                break label296;
                                                            }

                                                            var10005 = "onMessageDelivery, message id:";

                                                            try {
                                                                var72 = var72.append(var10005).append(var2);
                                                            } catch (RuntimeException var23) {
                                                                var63 = var23;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var24) {
                                                                var62 = var24;
                                                                var64 = false;
                                                                break label296;
                                                            }

                                                            var10005 = ", status:";

                                                            try {
                                                                var72 = var72.append(var10005).append(var59);
                                                            } catch (RuntimeException var21) {
                                                                var63 = var21;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var22) {
                                                                var62 = var22;
                                                                var64 = false;
                                                                break label296;
                                                            }

                                                            var10005 = ", transactionId: ";

                                                            try {
                                                                HMSLog.i(var10003, var72.append(var10005).append(var4).toString());
                                                            } catch (RuntimeException var19) {
                                                                var63 = var19;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var20) {
                                                                var62 = var20;
                                                                var64 = false;
                                                                break label296;
                                                            }

                                                            var10003 = "push.deliveryMessage";

                                                            try {
                                                                var68.a(var10003, var4, var59);
                                                                var70.onMessageDelivered(var67, new SendException(var59));
                                                                return;
                                                            } catch (RuntimeException var17) {
                                                                var63 = var17;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var18) {
                                                                var62 = var18;
                                                                var64 = false;
                                                            }
                                                        }
                                                    } else {
                                                        label297: {
                                                            var66 = "server_deleted_message";

                                                            try {
                                                                var69 = var66.equals(var3);
                                                            } catch (RuntimeException var41) {
                                                                var63 = var41;
                                                                var64 = false;
                                                                break label330;
                                                            } catch (Exception var42) {
                                                                var62 = var42;
                                                                var64 = false;
                                                                break label297;
                                                            }

                                                            if (var69) {
                                                                label298: {
                                                                    var70 = this;
                                                                    var67 = "HmsMessageService";

                                                                    StringBuilder var71;
                                                                    try {
                                                                        var71 = new StringBuilder();
                                                                    } catch (RuntimeException var31) {
                                                                        var63 = var31;
                                                                        var64 = false;
                                                                        break label330;
                                                                    } catch (Exception var32) {
                                                                        var62 = var32;
                                                                        var64 = false;
                                                                        break label298;
                                                                    }

                                                                    var10003 = "delete message, message id:";

                                                                    try {
                                                                        HMSLog.i(var67, var71.append(var10003).append(var2).toString());
                                                                        var70.onDeletedMessages();
                                                                        return;
                                                                    } catch (RuntimeException var29) {
                                                                        var63 = var29;
                                                                        var64 = false;
                                                                        break label330;
                                                                    } catch (Exception var30) {
                                                                        var62 = var30;
                                                                        var64 = false;
                                                                    }
                                                                }
                                                            } else {
                                                                label299: {
                                                                    var66 = "batchSent";

                                                                    try {
                                                                        var69 = var66.equals(var3);
                                                                    } catch (RuntimeException var39) {
                                                                        var63 = var39;
                                                                        var64 = false;
                                                                        break label330;
                                                                    } catch (Exception var40) {
                                                                        var62 = var40;
                                                                        var64 = false;
                                                                        break label299;
                                                                    }

                                                                    if (var69) {
                                                                        try {
                                                                            this.b(var1);
                                                                            return;
                                                                        } catch (RuntimeException var33) {
                                                                            var63 = var33;
                                                                            var64 = false;
                                                                            break label330;
                                                                        } catch (Exception var34) {
                                                                            var62 = var34;
                                                                            var64 = false;
                                                                        }
                                                                    } else {
                                                                        label300: {
                                                                            var66 = "HmsMessageService";

                                                                            StringBuilder var74;
                                                                            try {
                                                                                var74 = new StringBuilder();
                                                                            } catch (RuntimeException var37) {
                                                                                var63 = var37;
                                                                                var64 = false;
                                                                                break label330;
                                                                            } catch (Exception var38) {
                                                                                var62 = var38;
                                                                                var64 = false;
                                                                                break label300;
                                                                            }

                                                                            var65 = "Receive unknown message: ";

                                                                            try {
                                                                                HMSLog.e(var66, var74.append(var65).append(var3).toString());
                                                                                return;
                                                                            } catch (RuntimeException var35) {
                                                                                var63 = var35;
                                                                                var64 = false;
                                                                                break label330;
                                                                            } catch (Exception var36) {
                                                                                var62 = var36;
                                                                                var64 = false;
                                                                            }
                                                                        }
                                                                    }
                                                                }
                                                            }
                                                        }
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

                Exception var60 = var62;
                HMSLog.e("HmsMessageService", "handle intent exception: " + var60.getMessage());
                return;
            }

            RuntimeException var61 = var63;
            HMSLog.e("HmsMessageService", "handle intent RuntimeException: " + var61.getMessage());
        }
    }

}
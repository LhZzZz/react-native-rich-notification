package com.doubleyolk.richnotification;


import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;
import com.huawei.hms.push.RemoteMessage;

public class HwPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    private static final String MESSAGE_TAG = "RemoteMessage";
    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token);
        Log.i(TAG, "receive new token:" + token);
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
package com.doubleyolk.richnotification;

import android.util.Log;

import com.hihonor.push.sdk.HonorMessageService;
import com.hihonor.push.sdk.HonorPushDataMsg;

public class HonorPushService extends HonorMessageService {
    private static final String TAG = "HonorPushDemoLog";

    @Override
    public void onNewToken(String pushToken) {
        // TODO: 收到新的PushToken。
        Log.i(TAG, "receive new token:" + pushToken);
    }

    @Override
    public void onMessageReceived(HonorPushDataMsg msg) {
        // TODO: 收到透传消息。
    }
}

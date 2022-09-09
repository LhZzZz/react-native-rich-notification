package com.doubleyolk.richnotification;


import android.os.Bundle;
import android.util.Log;

import com.huawei.hms.push.HmsMessageService;

public class HwPushService extends HmsMessageService {
    private static final String TAG = "PushDemoLog";
    @Override
    public void onNewToken(String token, Bundle bundle) {
        super.onNewToken(token);
        System.out.println("lhz: "+token);
        Log.i(TAG, "receive new token:" + token);
    }
}
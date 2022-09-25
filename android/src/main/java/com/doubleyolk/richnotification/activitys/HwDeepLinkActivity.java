package com.doubleyolk.richnotification.activitys;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class HwDeepLinkActivity extends Activity {
    private static final String TAG = "HwDeepLinkData";

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getIntentData(getIntent());
    }

    @Override
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getIntentData(intent);
    }

    private void getIntentData(Intent intent) {
        if (null != intent) {
            // 获取data里的值
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                for (String key : bundle.keySet()) {
                    String content = bundle.getString(key);
                    Log.i(TAG, "receive data from push, key = " + key + ", content = " + content);
                }
            }
        } else {
            Log.i(TAG, "intent is null");
        }
    }
}

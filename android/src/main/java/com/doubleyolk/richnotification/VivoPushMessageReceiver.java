package com.doubleyolk.richnotification;

import android.content.Context;
import android.util.Log;

import com.vivo.push.model.UPSNotificationMessage;
import com.vivo.push.model.UnvarnishedMessage;
import com.vivo.push.sdk.OpenClientPushMessageReceiver;

public class VivoPushMessageReceiver extends OpenClientPushMessageReceiver {
    public VivoPushMessageReceiver() {
        super();
    }

    @Override
    public void onReceiveRegId(Context context, String regId) {
        Log.i("VIVO Recived id",regId);
        super.onReceiveRegId(context, regId);
    }

    @Override
    public void onTransmissionMessage(Context context, UnvarnishedMessage unvarnishedMessage) {
        super.onTransmissionMessage(context, unvarnishedMessage);
    }

    @Override
    public void onNotificationMessageClicked(Context context, UPSNotificationMessage msg) {
        super.onNotificationMessageClicked(context, msg);
    }
}

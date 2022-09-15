package com.doubleyolk.richnotification;

import android.content.Context;
import android.util.Log;

import com.heytap.msp.push.callback.ICallBackResultService;
import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;

public class OppoPushService extends DataMessageCallbackService implements ICallBackResultService {
    private static final String TAG = "OppoPushLog";
    private static final String MESSAGE_TAG = "Oppo Remote message";
    @Override
    public void onRegister(int i, String s) {
        Log.i(TAG, "receive new token:" + s);
    }

    @Override
    public void onUnRegister(int i) {

    }

    @Override
    public void onSetPushTime(int i, String s) {

    }

    @Override
    public void onGetPushStatus(int i, int i1) {

    }

    @Override
    public void onGetNotificationStatus(int i, int i1) {

    }

    @Override
    public void onError(int i, String s) {

    }

    @Override
    public void processMessage(Context context, DataMessage message) {
        super.processMessage(context, message);
        Log.i(MESSAGE_TAG, "message" + message.getTitle());
    }
}

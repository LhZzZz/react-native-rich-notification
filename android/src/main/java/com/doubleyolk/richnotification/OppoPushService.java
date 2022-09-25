package com.doubleyolk.richnotification;

import android.content.Context;
import android.content.Intent;
import android.os.Binder;
import android.os.Build;
import android.os.IBinder;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;
import com.heytap.msp.push.callback.ICallBackResultService;
import com.heytap.msp.push.mode.DataMessage;
import com.heytap.msp.push.service.DataMessageCallbackService;
import com.heytap.msp.push.HeytapPushManager;

public class OppoPushService extends DataMessageCallbackService implements ICallBackResultService {
    private static final String TAG = "OppoPushLog";
    private static final String MESSAGE_TAG = "Oppo Remote message";

    private ReactContext context;
    private String registerId;
    private final IBinder mBinder = new OppoPushService.LocalBinder();

    public OppoPushService(){
        super();
    }


    public class LocalBinder extends Binder {
        public OppoPushService getService() {
            // Return this instance of LocalService so clients can call public methods
            return OppoPushService.this;
        }

        public String getCachedRegisterId() {
            return registerId;
        }

    }

    @Override
    public IBinder onBind(Intent intent) {
        return mBinder;
    }

    @Override
    public void onRegister(int i, String s) {
        registerId = s;
        Log.i(TAG, "receive new registerId:" + s);
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


    public void initService(ReactContext context){
        this.context = context;
    }

    /**
     * 获取registerId
     * @param callback
     */
    public void getRegisterId(Callback callback){
        if (registerId!=null){
            Log.i(TAG,"has cached token");
            WritableMap map = Arguments.createMap();
            map.putString("data",registerId);
            map.putString("brand", Build.BRAND);
            if (callback!=null){
                callback.invoke(map);
            }
            return;
        }
        registerId =  HeytapPushManager.getRegisterID();
        Log.i(TAG,"getRegisterID"+ this.registerId);
        WritableMap map = Arguments.createMap();
        map.putString("data",registerId);
        map.putString("brand", Build.BRAND);
        if (callback!=null){
            callback.invoke(map);
        }
    }
}

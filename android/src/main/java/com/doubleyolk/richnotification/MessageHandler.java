package com.doubleyolk.richnotification;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import androidx.annotation.NonNull;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.WritableMap;
import com.facebook.react.modules.core.DeviceEventManagerModule;

public class MessageHandler extends ReactContextBaseJavaModule {
    private ReactContext context;
    private boolean hasNewMessageToCheck = false;
    private Intent currentIntent;  //当前收到的initent
    public MessageHandler(){

    }

    @NonNull
    @Override
    public String getName() {
        return "MessageStore";
    }

    /**
     * 初始化消息处理，传入rn context，在这里处理app的离线消息
     * * @param context
     */
    public void initMessageHandler(ReactContext context){
        this.context = context;
        new Handler().postDelayed(new Runnable() {
        @Override
        public void run() {
                //do something
            handlerOfflineMessage();
            }
        }, 2000);
    }

    public void handlerOfflineMessage(){
        Log.i("HandleIntent", "handlerOfflineMessage");
        if (currentIntent!=null){
            this.HandleIntent(currentIntent);
        }
    }

    public void HandleIntent(Intent intent){
        currentIntent = intent;
        hasNewMessageToCheck = true;
        if (this.context != null){//如果已经react native的context初始化完成了才可以调用js代码
            currentIntent = null;
            if (null != intent) {
                // 获取data里的值
                Bundle bundle = intent.getExtras();

                //如果intent里带标志是一条推送intent key
                if (bundle != null) {
                    WritableMap map = Arguments.createMap();
                    for (String key : bundle.keySet()) {
                        String content = bundle.getString(key);
                        map.putString(key, content);
                        Log.i("HandleIntent", "key = " + key + ", content = " + content);
                    }

                    this.context.getJSModule(DeviceEventManagerModule.RCTDeviceEventEmitter.class)
                            .emit("Hw_msg", map);
                }
            } else {
                Log.i("HandleIntent", "intent is null");
            }
            hasNewMessageToCheck = false;
        }
    }


}

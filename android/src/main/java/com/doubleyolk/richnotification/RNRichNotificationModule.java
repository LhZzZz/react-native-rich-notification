
package com.doubleyolk.richnotification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Build;
import android.os.Bundle;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.util.Log;

import com.facebook.react.bridge.Arguments;
import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.facebook.react.bridge.WritableMap;
import com.heytap.msp.push.HeytapPushManager;
import com.hihonor.push.sdk.HonorPushClient;
import com.huawei.hms.aaid.HmsInstanceId;
import com.meizu.cloud.pushsdk.PushManager;

public class RNRichNotificationModule extends ReactContextBaseJavaModule {

  private static final String TAG = "RNRichNotification";
  private final ReactApplicationContext reactContext;
  private HwPushService hwPushService;//华为推送服务类
  private Messenger mMessenger;

  public RNRichNotificationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    //初始化荣耀的推送服务
    HonorPushClient.getInstance().init(this.reactContext, true);
    //初始化OPPO的推送服务
    HeytapPushManager.requestNotificationPermission();
    HeytapPushManager.init(this.reactContext,true);
    //注册OPPO服务
    HeytapPushManager.register(this.reactContext,"fdc3536af7d6454498f4b5ab8bd9145a","bdb053b0645a4259aef34def720a893a",new OppoPushService());
    //注册魅族推送服务
    PushManager.register(this.reactContext,"150022","2269bbfee74641df96b225460e1592b8");

    Intent intent = new Intent(reactContext, HwPushService.class);
    reactContext.bindService(intent, hwPushConnection, Context.BIND_AUTO_CREATE);
  }

  @Override
  public String getName() {
    return "RNRichNotification";
  }

  /**
   * 初始化推送服务
   */
  @ReactMethod
  public void initPush(){

  }

  /**
   * 获取设备的厂商注册ID
   * @param callback
   */
  @ReactMethod
  public void getRegisterId(Callback callback){
    Log.i(TAG, Build.BRAND);
    String registerId = Build.BRAND;
    hwPushService.getToken(callback, reactContext);
  }


  /**
   * hw的service连接，用于获取service实例
   */
  private ServiceConnection hwPushConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className,
                                   IBinder service) {
      HwPushService.LocalBinder binder = (HwPushService.LocalBinder) service;
      hwPushService = binder.getService();
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {

    }
  };

}

package com.doubleyolk.richnotification;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.heytap.msp.push.HeytapPushManager;
import com.hihonor.push.sdk.HonorPushClient;

public class RNRichNotificationModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNRichNotificationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    //初始化荣耀的推送服务
    HonorPushClient.getInstance().init(this.reactContext, true);
    //初始化OPPO的推送服务
    HeytapPushManager.init(this.reactContext,true);
    //注册服务
    HeytapPushManager.register(this.reactContext,"fdc3536af7d6454498f4b5ab8bd9145a","bdb053b0645a4259aef34def720a893a",new OppoPushService());
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
}
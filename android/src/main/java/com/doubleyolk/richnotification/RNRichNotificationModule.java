
package com.doubleyolk.richnotification;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.Callback;
import com.hihonor.push.sdk.HonorPushClient;

public class RNRichNotificationModule extends ReactContextBaseJavaModule {

  private final ReactApplicationContext reactContext;

  public RNRichNotificationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    HonorPushClient.getInstance().init(reactContext, true);
  }

  @Override
  public String getName() {
    return "RNRichNotification";
  }
}
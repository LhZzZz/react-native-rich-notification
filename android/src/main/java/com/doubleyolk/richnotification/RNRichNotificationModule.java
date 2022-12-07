
package com.doubleyolk.richnotification;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.Binder;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
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
import com.hihonor.push.sdk.HonorPushCallback;
import com.hihonor.push.sdk.HonorPushClient;
import com.huawei.hms.aaid.HmsInstanceId;
import com.meizu.cloud.pushsdk.PushManager;

import java.util.HashMap;
import java.util.Map;

public class RNRichNotificationModule extends ReactContextBaseJavaModule {

  private static final String TAG = "RNRichNotification";
  private final ReactApplicationContext reactContext;
  private HwPushService hwPushService;//华为推送服务对象, 可访问service中的公共方法, 弃用!!!
  private HwPushHandler hwPushHandler;//华为推送服务客户端，与服务端通信

  private OppoPushService oppoPushService;//oppo推送服务对象, 可访问service中的公共方法
  private OppoPushService.LocalBinder oppoBinder; //oppo的binder对象, 可访问service的资源

  private VivoPushHandler vivoPushHandler;
  private HonorPushHandler honorPushHandler;
  private MiMessageHandler miMessageHandler;


  private String brand = Build.BRAND;;//手机厂商名

  public RNRichNotificationModule(ReactApplicationContext reactContext) {
    super(reactContext);
    this.reactContext = reactContext;
    //OPPO获取推送权限
    if (brand.equals("OPPO")){
      HeytapPushManager.requestNotificationPermission();
    }
  }

  @Override
  public String getName() {
    return "RNRichNotification";
  }

  @Override
  public Map<String, Object> getConstants() {
    final Map<String, Object> constants = new HashMap<>();
    constants.put("brand", Build.BRAND);

    return constants;
  }

  /**
   * 初始化推送服务
   * @param appId
   * @param appKey
   * @param appSecret
   */
  @ReactMethod
  public void initPush(String appId, String appKey, String appSecret, Callback callback){
    WritableMap map = Arguments.createMap();
    String msg = "暂不支持该手机厂商";
    if (brand.equals("HUAWEI")){
      Log.i(TAG,appId + " "+appKey +" "+ appSecret);
      hwPushHandler = new HwPushHandler(reactContext);
      hwPushHandler.initService();
      msg = "初始化华为推送服务";
    }else if (brand.equals("OPPO")){
      //初始化OPPO的推送服务
      HeytapPushManager.init(this.reactContext,true);
      //注册OPPO服务
      HeytapPushManager.register(this.reactContext,appKey,appSecret,new OppoPushService());
      Intent intent = new Intent(reactContext, OppoPushService.class);
      reactContext.bindService(intent, oppoPushServiceConnection, Context.BIND_AUTO_CREATE);
      msg = "初始化oppo推送服务";
    }else if (brand.equals("HONOR")){
      //初始化荣耀的推送服务
      honorPushHandler = new HonorPushHandler(reactContext);
      honorPushHandler.initService();
      msg = "初始化荣耀的推送服务";
    }else if(brand.equals("vivo")){
      vivoPushHandler = new VivoPushHandler(reactContext);
      vivoPushHandler.initService();
      msg = "初始化vivo的推送服务";
    }else if (brand.equals("Xiaomi")){
      miMessageHandler = new MiMessageHandler(reactContext);
      miMessageHandler.initService(appId,appKey);
    }
    Log.i(TAG,brand);
    map.putString("msg",msg);
    if (callback != null){
      callback.invoke(map);
    }
  }

  /**
   * 获取设备的厂商注册ID
   * @param callback
   */
  @ReactMethod
  public void getRegisterId(Callback callback){
    if (brand.equals("HUAWEI")){
      hwPushHandler.getDeviceToken(callback);
    }else if (brand.equals("OPPO")){
      if (oppoPushService != null){
        oppoPushService.getRegisterId(callback);
        Log.i(TAG,"binder register"+ oppoBinder.getCachedRegisterId());
      }
    }else if (brand.equals("HONOR")){
      honorPushHandler.getRegisterId(callback);
    }else if(brand.equals("vivo")){
      vivoPushHandler.getRegisterId(callback);
    }else if(brand.equals("Xiaomi")){
      miMessageHandler.getRegisterId(callback);
    }else {
      WritableMap map = Arguments.createMap();
      map.putString("data","");
      map.putString("brand", Build.BRAND);
      callback.invoke(map);
    }
  }

  @ReactMethod
  public void setBadge(int badge){
    if (brand.equals("HUAWEI")){
      hwPushHandler.setBadge(badge);
    }
  }


  /**
   * 华为的service连接，用于获取service实例
   * @deprecated 该获取service的方式, 已舍弃
   */
  private ServiceConnection hwPushConnection = new ServiceConnection() {

    @Override
    public void onServiceConnected(ComponentName className, IBinder service) {
      HwPushService.LocalBinder binder = (HwPushService.LocalBinder) service;
      hwPushService = binder.getService();
      hwPushService.initService(reactContext);
    }

    @Override
    public void onServiceDisconnected(ComponentName arg0) {

    }
  };

  private ServiceConnection oppoPushServiceConnection = new ServiceConnection() {
    @Override
    public void onServiceConnected(ComponentName componentName, IBinder iBinder) {
      oppoBinder = (OppoPushService.LocalBinder) iBinder;
      oppoPushService = oppoBinder.getService();
      oppoPushService.initService(reactContext);
    }

    @Override
    public void onServiceDisconnected(ComponentName componentName) {

    }
  };


}

# react-native-rich-notification

**集成各Android厂商的推送服务，仅支持Android端使用;**

| 厂商 | 接入SDK | RN端封装 |
| :--: | :-----: | :------: |
| 华为 |    ✓    |    ✓     |
| oppo |    ✓    |    ✓     |
| 魅族 |    ✓    |    —     |
| 荣耀 |    ✓    |    —     |
| 小米 |    —    |    —     |
| vivo |    —    |    —     |
目前在自己项目进行调试，会在近期陆续完善，有问题可联系[szdoubleyolk@163.com](https://note.youdao.com/)。
> 注：小米需要app上线后才可接入其推送服务


## 安装

`$ npm install react-native-rich-notification --save`

## React Native link
react native <= 0.59.x 需手动 link

`$ react-native link react-native-rich-notification`

## 手动配置

#### 通知栏消息配置

MainActivity中做如下修改

```java
import com.doubleyolk.richnotification.MessageHandler;// add this
import com.facebook.react.ReactInstanceManager;// add this

public class MainActivity extends ReactActivity {

  private static MessageHandler messageHandler;// add this
  private ReactInstanceManager mReactInstanceManager;// add this

  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    messageHandler = new MessageHandler();//add this
    getIntentData(getIntent());//add this
		initMessageHandler();// add this
  }

  @Override
  public void onNewIntent(Intent intent){
    super.onNewIntent(intent);
    setIntent(intent);// add this
    getIntentData(intent);// add this
  }
  
  // add this
  public void initMessageHandler(){
    mReactInstanceManager = getReactNativeHost().getReactInstanceManager();
    if (null == mReactInstanceManager.getCurrentReactContext()) {
      mReactInstanceManager.addReactInstanceEventListener(new ReactInstanceManager.ReactInstanceEventListener() {
        @Override
        public void onReactContextInitialized(ReactContext context) {
          messageHandler.initMessageHandler(context);
          mReactInstanceManager.removeReactInstanceEventListener(this);
        }
      });
    }else{
      ReactContext context  =  mReactInstanceManager.getCurrentReactContext();
      messageHandler.initMessageHandler(context);
    }
  }

  // add this
  private void getIntentData(Intent intent) {
    this.messageHandler.HandleIntent(intent);
  }

  @Override
  protected void onDestroy() {
    super.onDestroy();
  }
}

```


#### 华为

1. 按官方的Android接入文档进行以下步骤：https://developer.huawei.com/consumer/cn/doc/development/HMSCore-Guides/android-config-agc-0000001050170137  

   1. 配置AppGallery Connect
   2. 集成HMS Core SDK
   3. 配置混淆脚本

2. android/app/AndroidManifest.xml 添加

   ```xml
   <service android:name="com.doubleyolk.richnotification.HwPushService" android:exported="false">
       <intent-filter>
           <action android:name="com.huawei.push.action.MESSAGING_EVENT"/>
       </intent-filter>
   </service>
   ```

   ```xml
   <meta-data
       android:name="push_kit_auto_init_enabled"
       android:value="false" />
   ```

​		

## 使用

```javascript
import RNRichNotification from 'react-native-rich-notification';

// TODO: What to do with the module?
RNRichNotification;
```

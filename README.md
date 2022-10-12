
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
   <!-- 华为推送桌面角标权限 -->
       <uses-permission android:name="com.huawei.android.launcher.permission.CHANGE_BADGE" />
   
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

**OPPO**

 1. 按官方文档进行配置: https://open.oppomobile.com/new/developmentDoc/info?id=11221

 2. android/app/AndroidManifest.xml 修改为**"com.doubleyolk.richnotification.OppoPushService"**

    ```xml
    <service
        android:name="com.doubleyolk.richnotification.OppoPushService"
        android:permission="com.heytap.mcs.permission.SEND_PUSH_MESSAGE"
        android:exported="true">
        <intent-filter>
            <action android:name="com.heytap.mcs.action.RECEIVE_MCS_MESSAGE"/>
            <action android:name="com.heytap.msp.push.RECEIVE_MCS_MESSAGE"/>
        </intent-filter>
    </service>
    ```



## 使用

```javascript
import RichNotification, {
  InitPushConfig,
} from 'react-native-rich-notification';
import {
  Platform,
} from 'react-native';

const App = ()=>{
  
  //各平台所需的配置参数
  const initConfig: InitPushConfig = {
    oppo: {
      appKey: 'xxxxx',
      appSecret: 'xxxxx',
    },
    meizu: {appId: 'xxxxx', appSecret: 'xxxxx'},
  };
  
  useEffect(()=>{
    if(Platform.OS === "android"){
      //初始化推送服务,必须先初始化再调用其它方法
			RichNotification.initPush(initConfig, (res: initPushCallback) => {
        //获取设备注册ID
        RichNotification.getRegisterId((res: DeviceRegisterInfo) => {
          console.warn(res.brand, res.data);
        });
      });
      
      RichNotification.addNotificationTapListener((message: any) => {
        console.warn('点击了推送消息', message);
      });
    }
  },[])
}
```



## 方法

#### **initPush()**

初始化推送服务, 必须初始化后再调用其它方法。

**参数**

| 参数名 | 类型           | 必填 |
| ------ | -------------- | ---- |
| config | InitPushConfig | true |



#### addNotificationTapListener()

监听点击通知栏消息事件方法，包括app杀死后点击消息打开app，回调返回消息数据，消息体中的点击行为配置项必须是唤起MainActivity

**消息配置**

该方法需要后台在消息体中配置点击行为参数配合

1. 华为 

    `{click_action : 3}`

2. oppo :

    `{click_action_type:4, click_action_activity:"com.example.MainActivity"}`

   com.example.MainActivity替换成你的MainActivity

**参数**

| 参数名   | 类型     | 必填  |
| -------- | -------- | ----- |
| callback | function | false |

**例**

```javascript
RichNotification.addNotificationTapListener((message: any) => {
  console.warn('点击了推送消息', message);
});
```



#### addHuaweiRemoteMessageListener()

监听华为的透传消息，回调返回消息数据

**参数**

| 参数名   | 类型     | 必填  |
| -------- | -------- | ----- |
| callback | function | false |

**例**

```javascript
RichNotification.addHuaweiRemoteMessageListener((message: any) => {
	console.warn('透传消息', message);
});
```



#### removeListener()

移除监听

**例**

```javascript
const HwRemoteListener = RichNotification.addHuaweiRemoteMessageListener((message: any) => {
	console.warn('透传消息', message);
});

RichNotification.removeListener(HwRemoteListener)
```



#### getRegisterId()

获取设备注册ID，必须在初始化之后调用

**参数**

| 参数名   | 类型     | 必填  |
| -------- | -------- | ----- |
| callback | function | false |

**例**

```javascript
RichNotification.getRegisterId((res: DeviceRegisterInfo) => {
  console.warn(res.brand, res.data);
});
```



#### setBadge()

设置桌面角标，0即清除角标, 暂只华为支持

**参数**

| 参数名 | 类型   | 必填 |
| ------ | ------ | ---- |
| badge  | number | True |





```javascript
type ConfigInfo = {
    appId?:string,
    appKey?:string,
    appSecret?:string
}

type InitPushConfig = {
    /**
     * 华为可不传
     */
    huawei?:ConfigInfo,
    /**
     * oppo需要appkey,appsecret
     */
    oppo?:ConfigInfo,
    /**
     * 魅族需要appid,appsecret
     */
    meizu?:ConfigInfo,
    /**
     * 荣耀不用传，需要在AndroidManifest.xml配置appid即可
     */
    honor?:ConfigInfo,
    vivo?:ConfigInfo,
    mi?:ConfigInfo,
}
```






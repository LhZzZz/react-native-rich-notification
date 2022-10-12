


/**
 * 推送服务配置信息
 */
export type ConfigInfo = {
    appId?:string,
    appKey?:string,
    appSecret?:string
}


/**
 * 初始化配置信息
 */

export type InitPushConfig = {
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

/**
 * 手机的厂商推送服务注册信息
 */
export type DeviceRegisterInfo = {
    /**
     * device brand
     */
    brand:string,
    /**
     * register or token
     */
    data:string
}

/**
 * 初始化推送服务接口回调信息
 */
export type initPushCallback = {
    status:number
    msg:string
}

/**
 * 
 */

interface getRegisterIdCallback  {
    (callback:DeviceRegisterInfo):void
}

/**
 * 初始化推送回调方法
 */
interface initPushCall {
    (callback:initPushCallback):void
}

export default class RichNotification {
    /**
     * 监听点击通知栏消息回调事件，在点击通知栏消息后会触发
     * @param callback 
     */
    static addNotificationTapListener(callback):React.EmitterSubscription


    /**
     * 监听华为透传消息
     * @param callback 
     */
    static addHuaweiRemoteMessageListener(callback):React.EmitterSubscription
    
    /**
     * 移除监听事件
     * @param listener 
     */
    static removeListener(listener:React.EmitterSubscription)


    /**
     * 获取本机在本厂商推送的设备注册ID或者是设备token
     * 不同厂商叫法不一样
     * @param callback 
     */
    static getRegisterId(callback:getRegisterIdCallback)


    /**
     * 初始化推送服务, 必须先初始化后再调用其它方法
     * 不同厂商需要不同的参数，不用都传
     * @param appId 厂商平台注册的appid
     * @param appKey 厂商平台注册的appKey
     * @param appSecret 厂商平台注册的appSecret
     * @param callback 结果回调
     */
    static initPush(config:InitPushConfig,callback?:initPushCall)

    /**
     * 设置角标
     * @param badge 0即清除
     */
    static setBadge(badge:number)
    
    
}